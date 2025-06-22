package com.kasolution.aiohunterresources.data.network

import okhttp3.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url


interface FileDownloadService {
    @GET
    suspend fun downloadFile(@Url fileUrl: String): ResponseBody
}