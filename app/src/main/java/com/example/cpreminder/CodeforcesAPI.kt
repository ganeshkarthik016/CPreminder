package com.example.cpreminder

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data structures for JSON parsing
data class CFResponse(val status: String, val result: List<CFUser>)
data class CFUser(val handle: String, val rating: Int? = 0)
data class SubmissionResponse(val status: String, val result: List<CFSubmission>)
data class CFSubmission(val id: Long, val creationTimeSeconds: Long, val verdict: String)

interface CodeforcesService {
    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): CFResponse

    @GET("user.status")
    suspend fun getUserStatus(
        @Query("handle") handle: String,
        @Query("from") from: Int = 1,
        @Query("count") count: Int = 10
    ): SubmissionResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://codeforces.com/api/"
    val instance: CodeforcesService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CodeforcesService::class.java)
    }
}