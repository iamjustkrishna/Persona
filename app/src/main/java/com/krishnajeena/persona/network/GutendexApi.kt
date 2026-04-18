package com.krishnajeena.persona.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class GutendexBook(
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val subjects: List<String>,
    val formats: Map<String, String>,
    val download_count: Int
)

data class Author(
    val name: String,
    val birth_year: Int?,
    val death_year: Int?
)

data class GutendexResponse(
    val count: Int,
    val results: List<GutendexBook>
)

interface GutendexApiService {
    @GET("books")
    suspend fun getBooks(
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null,
        @Query("topic") topic: String? = null
    ): GutendexResponse

    @GET("books")
    suspend fun getPopularBooks(
        @Query("sort") sort: String = "popular"
    ): GutendexResponse
}

object RetrofitInstanceGutendex {
    private const val BASE_URL = "https://gutendex.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GutendexApiService by lazy {
        retrofit.create(GutendexApiService::class.java)
    }
}
