package com.kasolution.recursoshunter.data.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiClient {
    @POST("exec")
    @Headers("Content-Type: application/json")
    suspend fun peticion(@Body request: JsonObject): Response<JsonElement>
}
