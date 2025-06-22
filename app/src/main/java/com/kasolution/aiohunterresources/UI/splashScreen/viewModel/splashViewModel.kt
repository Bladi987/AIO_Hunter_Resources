package com.kasolution.aiohunterresources.UI.splashScreen.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.network.ProgressInterceptor
import com.kasolution.aiohunterresources.domain.splashScreen.FileDownloadUseCase
import com.kasolution.aiohunterresources.domain.splashScreen.getSettingsUseCase
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class  SplashViewModel(application: Application) : AndroidViewModel(application) {
    val log = "BladiDevSplashViewModel"
    val exception = MutableLiveData<String>()
    val dataSettings = MutableLiveData<Result<Pair<String?, String?>>>()
    val isloading = MutableLiveData<Boolean>() // Indicador de carga
    val errorMessage = MutableLiveData<String?>() // Para manejar errores

    val downloadStatus = MutableLiveData<Boolean>()
    val downloadProgress = MutableLiveData<Int>()
    val downloadStatusText = MutableLiveData<String>()
    val remainingTime = MutableLiveData<Long>()
    val totalBytesFile = MutableLiveData<Long>()
    val downloadMessage = MutableLiveData<String>()
    val filePath = MutableLiveData<String>()
    private var getSettingsUsecase = getSettingsUseCase()
    private var fileDownloadUseCase = FileDownloadUseCase(application.applicationContext)
    fun onCreate(urlId: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getSettingsUsecase(urlId)
                Log.d(log, "response: $response")
                if (response.isSuccess) {
                    response.getOrNull()?.let { data ->
                        Log.d(log, "data: $data")
                        dataSettings.postValue(Result.success(data))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        dataSettings.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun downloadFile(fileUrl: String) {
        Log.d("ViewModel", "Entrando a downloadFile con URL: $fileUrl")
        val progressListener = object : ProgressInterceptor.ProgressListener {
            override fun onProgress(progress: Int, bytesDownloaded: Long, totalBytes: Long, estimatedTimeRemaining: Long) {
                downloadProgress.postValue(progress)
                downloadStatusText.postValue(formatBytes(bytesDownloaded, totalBytes))
                remainingTime.postValue(estimatedTimeRemaining)
                totalBytesFile.postValue(totalBytes)
                downloadMessage.postValue(formatBytes(bytesDownloaded, totalBytes))
            }
        }

        viewModelScope.launch {
            val result = fileDownloadUseCase.downloadFile(fileUrl, progressListener)
            if (result.isSuccess) {
                val file = result.getOrNull()
                file?.let {
                    filePath.postValue(it.absolutePath)
                    downloadStatus.postValue(true)
                } ?: run {
                    downloadStatus.postValue(false)
                }
            } else {
                downloadStatus.postValue(false)
            }
        }
    }
    fun reiniciarProgreso(){
        downloadProgress.postValue(0)
    }
    private fun formatBytes(bytesDownloaded: Long, totalBytes: Long): String {
        val df = DecimalFormat("#.##")
        val kbDownloaded = bytesDownloaded / 1024.0
        val kbTotal = totalBytes / 1024.0
        return "Descargando (${df.format(kbDownloaded)} KB / ${df.format(kbTotal)} KB)"
    }
}