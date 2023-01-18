package com.example.mysql.retrofit

import com.example.mysql.data.User
import com.example.mysql.data.Users
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @Headers(
        "Accept: application/vnd.github+json",
        "Authorization: Bearer Personal access tokens (classic)",
        "X-GitHub-Api-Version: 2022-11-28"
    )

    @GET("users")
    suspend fun getUsersList(
        @Query("since") since: Int,
        @Query("per_page") per_page: Int
    ): Response<List<Users>>

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String,
    ): Response<User>
}