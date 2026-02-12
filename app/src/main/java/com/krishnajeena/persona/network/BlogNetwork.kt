package com.krishnajeena.persona.network

import com.google.gson.annotations.SerializedName
import com.krishnajeena.persona.data_layer.ArticleCategoriesResponse
import com.krishnajeena.persona.data_layer.BlogResponse
import com.krishnajeena.persona.data_layer.DevToArticle
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogApiService {
    @GET("thekrishnajeena/KrishnaJeena/refs/heads/main/personadata/categoryBlogs.json")
    suspend fun getCategories(): BlogResponse

    @GET("thekrishnajeena/KrishnaJeena/refs/heads/main/personadata/categoryBlogs.json")
    suspend fun getArticlesCategories(): ArticleCategoriesResponse

    @GET("articles") // Or your specific endpoint
    suspend fun getArticlesByTag(
        @Query("tag") tag: String
    ): List<DevToArticle>
}



object RetrofitInstance {
    private const val BASE_URL = "https://raw.githubusercontent.com/"

    val api: BlogApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(BlogApiService::class.java)
    }
}


interface FounderApiService {
    @GET("iamjustkrishna/quote-api/refs/heads/main/founder_articles.json")
    suspend fun getFounderArticles(): List<FounderArticleDto>
}

data class FounderArticleDto(
    val title: String,
    val url: String,
    val snippet: String,
    @SerializedName("founder_name")
    val founderName: String,
    val image: String
)


object RetrofitInstanceFounder {
    // Note: Base URL must end with /
    private const val BASE_URL = "https://raw.githubusercontent.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: FounderApiService by lazy {
        retrofit.create(FounderApiService::class.java)
    }
}
