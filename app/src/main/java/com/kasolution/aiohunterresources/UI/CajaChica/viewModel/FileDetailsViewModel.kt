package com.kasolution.aiohunterresources.UI.CajaChica.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.cajaChica.deleteFileSheetUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.getFileDetailsUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.insertFileSheetUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.updateFileSheetUseCase
import kotlinx.coroutines.launch


class FileDetailsViewModel : ViewModel() {
    val FileDetailsModel = MutableLiveData<ArrayList<fileDetails>>()
    val isloading = MutableLiveData<Boolean>()
    val insertarFileSheet = MutableLiveData<fileDetails>()
    val updateFileSheet = MutableLiveData<fileDetails>()
    val deleteFileSheet = MutableLiveData<String>()
    var getFileDetailsUseCase = getFileDetailsUseCase()
    var insertFileSheetUseCase = insertFileSheetUseCase()
    var updateFileSheetUseCase = updateFileSheetUseCase()
    var deleteFileSheetUseCase = deleteFileSheetUseCase()


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

    fun onInsert(urlId: urlId, fileDetails: fileDetails) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = insertFileSheetUseCase(urlId, fileDetails)
            insertarFileSheet.postValue(response)
            isloading.postValue(false)
        }
    }

    fun onUpdate(urlId: urlId, fileDetails: fileDetails) {
        viewModelScope.launch {
            isloading.postValue(true)
            try {
                val response = updateFileSheetUseCase(urlId, fileDetails)
                updateFileSheet.postValue(response) // Notificar actualización
            } catch (e: Exception) {
                //exception.postValue(e.message)
            }

            isloading.postValue(false)
        }
    }

    fun onDelete(urlId: urlId, fileDetails: fileDetails) {
        viewModelScope.launch {
            isloading.postValue(true)
            try {
                val response = deleteFileSheetUseCase(urlId, fileDetails)
                deleteFileSheet.postValue(response)

            } catch (e: Exception) {
//                exception.postValue(e.message)
            }

            isloading.postValue(false)
        }
    }
}