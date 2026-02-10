package com.krishnajeena.persona.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.DevToArticle
import com.krishnajeena.persona.network.RetrofitInstance
import com.krishnajeena.persona.network.RetrofitInstanceExploreArticle
import com.krishnajeena.persona.network.RetrofitInstanceGemini
import com.krishnajeena.persona.network.SummaryRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
data class SummaryState(
    val content: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ArticlesViewModel : ViewModel() {

    // 1. Existing Article Discovery State
    var articles by mutableStateOf(listOf<DevToArticle>())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var selectedCategory by mutableStateOf("entrepreneurship")
        private set

    // 2. New AI Summary State
    var summaryState by mutableStateOf(SummaryState())
        private set

    /**
     * Fetches articles from Dev.to based on the selected tag.
     */
    fun fetchArticles(tag: String) {
        selectedCategory = tag
        viewModelScope.launch {
            isLoading = true
            try {
                // Using your existing Retrofit instance for Dev.to
                val response = RetrofitInstanceExploreArticle.api.getArticlesByTag(tag)
                articles = response
            } catch (e: Exception) {
                e.printStackTrace()
                articles = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Handles category selection and refreshes the feed.
     */
    fun onCategoryClick(tag: String) {
        if (selectedCategory != tag) {
            fetchArticles(tag)
        }
    }

    /**
     * Calls your Flask backend to get a Gemini 3 Flash summary of a specific URL.
     */
    fun summarizeArticle(articleUrl: String) {
        viewModelScope.launch {
            // Reset state to loading
            summaryState = SummaryState(isLoading = true)

            try {
                // Assuming you've created RetrofitInstanceGemini for your Flask API
                // and it returns an object with a 'summary' string.
                val result = RetrofitInstanceGemini.api.getSummary(SummaryRequest(url = articleUrl))

                summaryState = SummaryState(
                    content = result.summary,
                    isLoading = false
                )
            } catch (e: Exception) {
                summaryState = SummaryState(
                    error = "Could not generate summary. Check your connection.",
                    isLoading = false
                )
            }
        }
    }

    /**
     * Resets the summary state when the user closes the bottom sheet.
     */
    fun clearSummary() {
        summaryState = SummaryState()
    }
}