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
    var isDataLoaded = false
    val exception = MutableLiveData<String>()
    val FileModel = MutableLiveData<Result<ArrayList<file>>>()
    val isloading = MutableLiveData<Boolean>()
    val createDocument = MutableLiveData<Result<file>>()
    val updateDocument = MutableLiveData<Result<file>>()
    val deleteDocument = MutableLiveData<Result<String>>()
    var getFileUseCase = getFileUseCase()
    var createDocumentUseCase = createDocumentUseCase()
    var updateDocumentUseCase = updateDocumentUseCase()
    var deleteDocumentUseCase = deleteDocumentUseCase()

    fun onCreate(urlId: urlId) {
        if (!isDataLoaded) {
            viewModelScope.launch {
                try {
                    isloading.postValue(true)
                    val response = getFileUseCase(urlId)

                    if (response.isSuccess) {
                        response.getOrNull()?.let { lista ->
                            FileModel.postValue(Result.success(lista))
                            isDataLoaded = true
                        }
                    } else {
                        response.exceptionOrNull()?.let { ex ->
                            FileModel.postValue(Result.failure(ex))
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

    fun onRefresh(urlId: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getFileUseCase(urlId)
                if (response.isSuccess) {
                    response.getOrNull()?.let { lista ->
                        FileModel.postValue(Result.success(lista))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        FileModel.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun onCreateDocument(urlId: urlId, file: file, adicional: List<String>) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = createDocumentUseCase(urlId, file, adicional)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        createDocument.postValue(Result.success(registro)) // Notificar inserción
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        createDocument.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun onUpdateDocument(urlId: urlId, file: file) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = updateDocumentUseCase(urlId, file)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        updateDocument.postValue(Result.success(registro)) // Notificar actualización
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        updateDocument.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun onDeleteDocument(urlId: urlId, file: file) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = deleteDocumentUseCase(urlId, file)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        deleteDocument.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        deleteDocument.postValue(Result.failure(ex))
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