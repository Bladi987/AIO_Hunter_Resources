package com.kasolution.aiohunterresources.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.kasolution.aiohunterresources.core.RetrofitHelper
import com.kasolution.aiohunterresources.data.network.FileDownloadService
import com.kasolution.aiohunterresources.data.network.ProgressInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.File
import java.io.FileOutputStream

class FileDownloadRepository(private val context: Context) {

    suspend fun downloadFile(
        fileUrl: String,
        progressListener: ProgressInterceptor.ProgressListener
    ): File? = withContext(Dispatchers.IO) {
        // Creamos el interceptor para la barra de progreso
        val interceptor = ProgressInterceptor(progressListener)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        // Configuramos Retrofit para la descarga con el cliente personalizado
        val retrofit = RetrofitHelper.getRetrofitForDownload("https://drive.google.com/", client)
        val fileDownloadService = retrofit.create(FileDownloadService::class.java)

        return@withContext try {
            val body = fileDownloadService.downloadFile(fileUrl)
            body?.let {
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")
                it.byteStream().use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }
                file
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
