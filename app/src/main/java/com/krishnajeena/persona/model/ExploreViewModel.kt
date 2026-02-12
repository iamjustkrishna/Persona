package com.krishnajeena.persona.model

import android.app.Application
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.ArticleEntity
import com.krishnajeena.persona.data_layer.BlogCategory
import com.krishnajeena.persona.data_layer.BlogResponse
import com.krishnajeena.persona.data_layer.DevToArticle
import com.krishnajeena.persona.network.FounderArticleDto
import com.krishnajeena.persona.network.RetrofitInstance
import com.krishnajeena.persona.network.RetrofitInstanceExploreArticle
import com.krishnajeena.persona.network.RetrofitInstanceFounder
import com.krishnajeena.persona.network.RetrofitInstanceGemini
import com.krishnajeena.persona.network.SummaryRequest
import com.krishnajeena.persona.other.NetworkMonitor
import io.ktor.client.engine.cio.CIO

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.InternalAPI

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Unified State for AI Summary
data class SummaryState(
    val content: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
class ExploreViewModel(application: Application) : AndroidViewModel(application) {

    private val networkMonitor = NetworkMonitor(application.applicationContext)
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // --- UI STATES ---
    var categories by mutableStateOf<List<BlogCategory>>(emptyList())
        private set
    var articlesCategories by mutableStateOf<List<String>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var selectedCategory by mutableStateOf("")
        private set

    // --- INTERNAL DATA ---
    private var devToArticles by mutableStateOf<List<ArticleEntity>>(emptyList())
    private var founderInsights by mutableStateOf<List<ArticleEntity>>(emptyList())

    // --- AI SUMMARY STATE ---
    var summaryState by mutableStateOf(SummaryState())
        private set

    // --- THE TRUE RANDOM SPRINKLE ENGINE ---
    val combinedFeed by derivedStateOf {
        val result = mutableListOf<ArticleEntity>()

        // 1. Safe Shuffle
        val foundersQueue = founderInsights.shuffled().toMutableList()
        val totalArticles = devToArticles.size

        // 2. SAFE GAP CALCULATION
        // We add a check: if size is 0, we don't even run the logic.
        // If size > 0, we ensure averageGap is at least 1.
        val averageGap = if (foundersQueue.isNotEmpty()) {
            (totalArticles / foundersQueue.size).coerceAtLeast(1)
        } else {
            5 // Default fallback
        }

        devToArticles.forEachIndexed { index, article ->
            result.add(article)

            if (foundersQueue.isNotEmpty()) {
                // 3. SAFE RANDOM RANGE
                // If averageGap is small (like 1 or 2), averageGap + (-2..2) could be 0 or negative.
                // We use coerceAtLeast(1) to ensure the modulo (%) never hits 0.
                val randomOffset = (-2..2).random()
                val triggerPoint = (averageGap + randomOffset).coerceAtLeast(1)

                val shouldInsert = (index + 1) % triggerPoint == 0
                val forceInsert = (totalArticles - index) <= foundersQueue.size

                if (shouldInsert || forceInsert) {
                    result.add(foundersQueue.removeAt(0))
                }
            }
        }
        result
    }
    init {
        monitorNetwork()
    }

    private fun monitorNetwork() {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { connected ->
                _isConnected.value = connected
                if (connected && categories.isEmpty()) {
                    fetchAllDiscoveryMetadata()
                }
            }
        }
    }

    private fun fetchAllDiscoveryMetadata() {
        viewModelScope.launch {
            isLoading = true
            try {
                // PARALLEL CALL 1: Get Categories & Founder Insights from GitHub
                val categoriesDef = async { RetrofitInstance.api.getCategories() }
                val tagsDef = async { RetrofitInstance.api.getArticlesCategories() }
                val foundersDef = async { RetrofitInstanceFounder.api.getFounderArticles() }

                val categoryRes = categoriesDef.await()
                val tagsRes = tagsDef.await()
                val founderDtos = foundersDef.await()

                // ASSIGN METADATA
                categories = categoryRes.categories
                articlesCategories = tagsRes.articlesCategories
                founderInsights = founderDtos.map { it.toEntity() }
                Log.d("FOUNDERS: ", "$founderInsights")

                if (articlesCategories.isNotEmpty()) {
                    val initialTag = articlesCategories[0]
                    selectedCategory = initialTag
                    // PARALLEL CALL 2: Get initial articles from Dev.to
                    fetchArticlesByTag(initialTag)
                }
            } catch (e: Exception) {
                Log.e("PERSONA_DEBUG", "Metadata Fetch Failed: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun onCategorySelected(tag: String) {
        if (selectedCategory != tag) {
            selectedCategory = tag
            fetchArticlesByTag(tag)
        }
    }

    private fun fetchArticlesByTag(tag: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Hitting Dev.to API for the actual articles
                val response = RetrofitInstanceExploreArticle.api.getArticlesByTag(tag)
                devToArticles = response.map { it.toEntity(tag) }
            } catch (e: Exception) {
                Log.e("PERSONA_DEBUG", "Dev.to Fetch Failed: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // --- AI SUMMARY LOGIC ---
    fun summarizeArticle(articleUrl: String) {
        viewModelScope.launch {
            summaryState = SummaryState(isLoading = true)
            try {
                // Hitting your Vercel/Flask Backend
                val result = RetrofitInstanceGemini.api.getSummary(SummaryRequest(url = articleUrl))
                summaryState = SummaryState(content = result.summary)
            } catch (e: Exception) {
                summaryState = SummaryState(error = "Gemini is busy. Try again in a moment.")
            }
        }
    }

    fun clearSummary() { summaryState = SummaryState() }

    override fun onCleared() {
        super.onCleared()
        networkMonitor.unregisterCallback()
    }
}

fun FounderArticleDto.toEntity(): ArticleEntity {
    // This service finds their photo automatically based on their name!
    val autoPhotoUrl = "https://unavatar.io/twitter/${this.founderName.replace(" ", "")}"

    return ArticleEntity(
        url = this.url,
        title = this.title,
        description = this.snippet,
        coverImage = if (this.image.contains("unsplash")) autoPhotoUrl else this.image,
        source = "founder",
        founderName = this.founderName,
        category = "entrepreneurship"
    )
}

fun DevToArticle.toEntity(category: String): ArticleEntity = ArticleEntity(
    url = this.url,
    title = this.title,
    description = this.description,
    coverImage = this.cover_image, // Standardizing 'cover_image' name
    source = "dev.to",
    category = category
)