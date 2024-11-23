package com.kasolution.aiohunterresources.UI.CajaChica.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.cajaChica.getFileDetailsUseCase
import kotlinx.coroutines.launch


class FileDetailsViewModel : ViewModel() {
    val FileDetailsModel = MutableLiveData<ArrayList<fileDetails>>()
    val isloading = MutableLiveData<Boolean>()
    var getFileDetailsUseCase = getFileDetailsUseCase()

    fun onCreate(urlId: urlId) {
        if (FileDetailsModel.value.isNullOrEmpty()) {
            viewModelScope.launch {
                isloading.postValue(true)
                val response = getFileDetailsUseCase(urlId)
                if (response.isNotEmpty()) {
                    FileDetailsModel.postValue(response)
                }
                isloading.postValue(false)
            }
        }
    }

    fun onRefresh(urlId: urlId) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = getFileDetailsUseCase(urlId)
            if (response.isNotEmpty()) {
                FileDetailsModel.postValue(response)
            }
            isloading.postValue(false)
        }
    }
}