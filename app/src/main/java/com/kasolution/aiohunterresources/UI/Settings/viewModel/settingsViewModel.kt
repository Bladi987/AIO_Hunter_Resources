package com.kasolution.aiohunterresources.UI.Settings.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.Settings.view.model.itemContactUs
import com.kasolution.aiohunterresources.UI.Settings.view.model.userKey
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.settings.insertMessageUseCase
import com.kasolution.aiohunterresources.domain.settings.setKeysUseCase
import kotlinx.coroutines.launch

class settingsViewModel : ViewModel() {
    val exception = MutableLiveData<String>()
    val listKeys = MutableLiveData<Result<String>>()
    val sendMessages = MutableLiveData<Result<itemContactUs>>()
    val isloading = MutableLiveData<Boolean>()
    val setKeysUseCase = setKeysUseCase()
    val insertMessageUseCase = insertMessageUseCase()
    fun setKeys(urlid: urlId,user:String, userKey: userKey) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = setKeysUseCase(urlid,user, userKey)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        listKeys.postValue(Result.success(registro))
                    }
                }else{
                    response.exceptionOrNull()?.let { ex ->
                        listKeys.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }
    fun insertMessage(urlid: urlId,itemContactUs: itemContactUs) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = insertMessageUseCase(urlid, itemContactUs)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        sendMessages.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        sendMessages.postValue(Result.failure(ex))
                    }
                }
            }catch (e:Exception){
                exception.postValue(e.message)
            }finally {
                isloading.postValue(false)
            }
        }
    }
}