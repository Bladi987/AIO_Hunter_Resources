package com.kasolution.recursoshunter.data.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiClient {

    //brandModule
    @POST("exec")
    @Headers("Content-Type: application/json")
    suspend fun peticion(@Body request: JsonObject): Response<JsonElement>

//    @POST("exec")
//    @Headers("Content-Type: application/json")
//    suspend fun getModel(@Body request: JsonObject): Response<JsonElement>
//
//    @POST("exec")
//    @Headers("Content-Type: application/json")
//    suspend fun insertModel(@Body request: JsonObject):Response<JsonElement>
//
//    @POST("exec")
//    @Headers("Content-Type: application/json")
//    suspend fun updateModel(@Body request: JsonObject):Response<JsonElement>
//
//    @POST("exec")
//    @Headers("Content-Type: application/json")
//    suspend fun deleteModel(@Body request: JsonObject):Response<JsonElement>
//    //secureModule
//
//    @POST("exec")
//    @Headers("Content-Type:application/json")
//    suspend fun login(@Body request: JsonObject):Response<JsonElement>
//
//    @POST("exec")
//    @Headers("Content-Type:application/json")
//    suspend fun getUser(@Body request: JsonObject):Response<JsonElement>
//
//    //userModule
//    @POST("exec")
//    @Headers("Content-Type: application/json")
//    suspend fun insertUser(@Body request: JsonObject):Response<JsonElement>
//
//    @POST("exec")
//    @Headers("Content-Type: application/json")
//    suspend fun updateUser(@Body request: JsonObject):Response<JsonElement>
//
//    @POST("exec")
//    @Headers("Content-Type: application/json")
//    suspend fun deleteUser(@Body request: JsonObject):Response<JsonElement>
//
//    //settingsModule
//    @POST("exec")
//    @Headers("Content-Type: application/json")
//    suspend fun getSettings(@Body request: JsonObject):Response<JsonElement>
}