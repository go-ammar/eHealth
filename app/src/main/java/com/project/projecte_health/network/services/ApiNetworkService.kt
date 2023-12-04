package com.project.projecte_health.network.services

import retrofit2.http.GET

interface ApiNetworkService {

    @GET("/repos/square/retrofit/stargazers")
    suspend fun getUserList(): List<String>

}