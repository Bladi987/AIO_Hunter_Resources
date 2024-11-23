package com.kasolution.aiohunterresources.UI.splashScreen.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.splashScreen.getSettingsUseCase
import kotlinx.coroutines.launch

class splashViewModel : ViewModel() {
    val log="BladiDevSplashViewModel"
    val versionAPP = MutableLiveData<String>()
    val isloading = MutableLiveData<Boolean>()
    var getSettingsUsecase= getSettingsUseCase()
    fun onCreate(urlId: urlId) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = getSettingsUsecase(urlId)
            versionAPP.postValue(response)
            isloading.postValue(false)
        }
    }
}