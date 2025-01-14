package com.kasolution.aiohunterresources.UI.CajaChica.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.cajaChica.createDocumentUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.deleteDocumentUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.getFileUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.updateDocumentUseCase
import kotlinx.coroutines.launch


class FileViewModel : ViewModel() {
    val FileModel = MutableLiveData<ArrayList<file>>()
    val isloading = MutableLiveData<Boolean>()
    val createDocument = MutableLiveData<file>()
    val updateDocument = MutableLiveData<file>()
    val deleteDocument = MutableLiveData<String>()
    var getFileUseCase = getFileUseCase()
    var createDocumentUseCase = createDocumentUseCase()
    var updateDocumentUseCase =updateDocumentUseCase()
    var deleteDocumentUseCase =deleteDocumentUseCase()

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
    fun onCreateDocument(urlId: urlId, file: file, adicional:List<String>) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = createDocumentUseCase(urlId, file,adicional)
            createDocument.postValue(response)
            isloading.postValue(false)
        }
    }

    fun onUpdateDocument(urlId: urlId, file: file) {
        viewModelScope.launch {
            isloading.postValue(true)
            try {
                val response = updateDocumentUseCase(urlId, file)
                updateDocument.postValue(response) // Notificar actualización
            } catch (e: Exception) {
                //exception.postValue(e.message)
            }

            isloading.postValue(false)
        }
    }

    fun onDeleteDocument(urlId: urlId, file: file) {
        viewModelScope.launch {
            isloading.postValue(true)
            try {
                val response = deleteDocumentUseCase(urlId, file)
                deleteDocument.postValue(response)

            } catch (e: Exception) {
//                exception.postValue(e.message)
            }

            isloading.postValue(false)
        }
    }
}