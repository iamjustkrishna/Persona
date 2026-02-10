package com.krishnajeena.persona.network

import com.krishnajeena.persona.data_layer.DevToArticle
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// The request body we send to Flask
data class SummaryRequest(
    val url: String
)

// The response we get back from Flask
data class SummaryResponse(
    val summary: String
)

interface DevToApi {
    @GET("articles")
    suspend fun getArticlesByTag(
        @Query("tag") tag: String
    ): List<DevToArticle>
}

interface GeminiApiService {
    @POST("summarize") // This matches the @app.route("/summarize") in your Flask code
    suspend fun getSummary(
        @Body request: SummaryRequest
    ): SummaryResponse
}

object RetrofitInstanceExploreArticle {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://dev.to/api/")
            .addConverterFactory(GsonConverterFactory.create()) // or Moshi if preferred
            .build()
    }

    val api: DevToApi by lazy {
        retrofit.create(DevToApi::class.java)
    }
}

object RetrofitInstanceGemini{
    private const val BASE_URL = "https://quote-api-one.vercel.app/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }
}
