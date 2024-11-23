package com.kasolution.aiohunterresources.UI.CajaChica.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.cajaChica.getFileUseCase
import kotlinx.coroutines.launch


class FileViewModel : ViewModel() {
    val FileModel = MutableLiveData<ArrayList<file>>()
    val isloading = MutableLiveData<Boolean>()
    var getFileUseCase = getFileUseCase()

    fun onCreate(urlId: urlId) {
        if (FileModel.value.isNullOrEmpty()) {
            viewModelScope.launch {
                isloading.postValue(true)
                val response = getFileUseCase(urlId)
                if (response.isNotEmpty()) {
                    FileModel.postValue(response)
                }
                isloading.postValue(false)
            }
        }
    }

    fun onRefresh(urlId: urlId) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = getFileUseCase(urlId)
            if (response.isNotEmpty()) {
                FileModel.postValue(response)
            }
            isloading.postValue(false)
        }
    }
}