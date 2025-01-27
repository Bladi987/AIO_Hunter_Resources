package com.kasolution.aiohunterresources.UI.splashScreen.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.splashScreen.getSettingsUseCase
import kotlinx.coroutines.launch

class splashViewModel : ViewModel() {
    val log = "BladiDevSplashViewModel"
    val exception = MutableLiveData<String>()
    val versionAPP = MutableLiveData<Result<String>>() // Contendrá la versión de la app
    val isloading = MutableLiveData<Boolean>() // Indicador de carga
    val errorMessage = MutableLiveData<String?>() // Para manejar errores
    private var getSettingsUsecase = getSettingsUseCase()
    fun onCreate(urlId: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getSettingsUsecase(urlId)
                if (response.isSuccess) {
                    response.getOrNull()?.let { data ->
                        versionAPP.postValue(Result.success(data))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        versionAPP.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }
}