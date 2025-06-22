package com.kasolution.aiohunterresources.domain.splashScreen

import android.content.Context
import android.util.Log
import com.kasolution.aiohunterresources.data.FileDownloadRepository
import com.kasolution.aiohunterresources.data.network.ProgressInterceptor
import java.io.File

class FileDownloadUseCase (val context: Context) {
    private val repository = FileDownloadRepository(context)
    suspend fun downloadFile(fileUrl: String, progressListener: ProgressInterceptor.ProgressListener): Result<File> {
        return try {
            val file = repository.downloadFile(fileUrl, progressListener)
            if (file != null) {
                Result.success(file)
            } else {
                Result.failure(Exception("Archivo nulo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}