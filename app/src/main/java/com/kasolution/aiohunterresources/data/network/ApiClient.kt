package com.kasolution.recursoshunter.data.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiClient {
    @POST("exec")
    @Headers("Content-Type: application/json")
    suspend fun peticion(@Body request: JsonObject): Response<JsonElement>
}