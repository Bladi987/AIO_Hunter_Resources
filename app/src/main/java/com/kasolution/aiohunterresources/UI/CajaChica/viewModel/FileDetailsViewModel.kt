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
    var isDataLoaded = false
    val FileDetailsModel = MutableLiveData<Result<ArrayList<fileDetails>>>()
    val exception = MutableLiveData<String>()
    val isloading = MutableLiveData<Boolean>()
    val insertarFileSheet = MutableLiveData<Result<fileDetails>>()
    val updateFileSheet = MutableLiveData<Result<fileDetails>>()
    val deleteFileSheet = MutableLiveData<Result<String>>()
    var getFileDetailsUseCase = getFileDetailsUseCase()
    var insertFileSheetUseCase = insertFileSheetUseCase()
    var updateFileSheetUseCase = updateFileSheetUseCase()
    var deleteFileSheetUseCase = deleteFileSheetUseCase()


    fun onCreate(urlId: urlId) {
        if (!isDataLoaded) {
            viewModelScope.launch {
                try {
                    isloading.postValue(true)
                    val response = getFileDetailsUseCase(urlId)
                    if (response.isSuccess) {
                        response.getOrNull()?.let { registro ->
                            FileDetailsModel.postValue(Result.success(registro))
                            isDataLoaded = true
                        }
                    } else {
                        response.exceptionOrNull()?.let { ex ->
                            FileDetailsModel.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
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
                val response = getFileDetailsUseCase(urlId)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        FileDetailsModel.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        FileDetailsModel.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun onInsert(urlId: urlId, fileDetails: fileDetails, adicional: List<String>) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = insertFileSheetUseCase(urlId, fileDetails, adicional)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        insertarFileSheet.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        insertarFileSheet.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun onUpdate(urlId: urlId, fileDetails: fileDetails, adicional: List<String>) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = updateFileSheetUseCase(urlId, fileDetails, adicional)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        updateFileSheet.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        updateFileSheet.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun onDelete(urlId: urlId, fileDetails: fileDetails) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = deleteFileSheetUseCase(urlId, fileDetails)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        deleteFileSheet.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        deleteFileSheet.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
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