package com.kasolution.aiohunterresources.UI.FichasTecnicas.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.fichasTecnicas.deleteModelUseCase
import com.kasolution.aiohunterresources.domain.fichasTecnicas.getModelUseCase
import com.kasolution.aiohunterresources.domain.fichasTecnicas.insertModelUseCase
import com.kasolution.aiohunterresources.domain.fichasTecnicas.updateModelUseCase

import kotlinx.coroutines.launch

class ModelViewModel : ViewModel() {
    var isDataLoaded = false
    val exception = MutableLiveData<String>()
    val vehicleModel = MutableLiveData<Result<ArrayList<VehicleModel>>>()
    val insertarModel = MutableLiveData<Result<VehicleModel>>()
    val updateModel = MutableLiveData<Result<VehicleModel>>()
    val deleteModel = MutableLiveData<Result<String>>()
    val isloading = MutableLiveData<Boolean>()
    var getModeloUseCase = getModelUseCase()
    var insertModelUseCase = insertModelUseCase()
    var updateModelUseCase = updateModelUseCase()
    var deleteModelUseCase = deleteModelUseCase()

    fun onCreate(urlId: urlId, filter: String, state: String) {
        if (!isDataLoaded) {
            viewModelScope.launch {
                try {
                    isloading.postValue(true)
                    val response = getModeloUseCase(urlId, filter, 1, state)
                    if (response.isSuccess) {
                        response.getOrNull()?.let { lista ->

                            if (lista.size == 1) {
                                if (lista[0].id.isNotEmpty()) vehicleModel.postValue(Result.success(lista))
                            } else {
                                vehicleModel.postValue(Result.success(lista))
                            }
                            isDataLoaded = true
                        }
                    } else {
                        response.exceptionOrNull()?.let { ex ->
                            vehicleModel.postValue(Result.failure(ex))
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

    fun onRefresh(urlId: urlId, filter: String, state: String) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getModeloUseCase(urlId, filter, 1, state)
                if (response.isSuccess) {
                    response.getOrNull()?.let { lista ->
                        if (lista.size == 1) {
                            if (lista[0].id.isNotEmpty()) vehicleModel.postValue(
                                Result.success(
                                    lista
                                )
                            )
                        } else vehicleModel.postValue(Result.success(lista))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        vehicleModel.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun insertModel(
        urlId: urlId,
        data: String,
        type: String,
        marca: String,
        modelo: String,
        comentarios: String,
        basico: String,
        otros: String,
        tecnico: String,
        aprobado: String,
        estado: String
    ) {
        val listaModel =
            listOf(data, type, marca, modelo, comentarios, basico, otros, tecnico, aprobado, estado)
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = insertModelUseCase(urlId, listaModel)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        insertarModel.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        insertarModel.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun updateModel(
        urlId: urlId,
        id: String,
        data: String,
        type: String,
        marca: String,
        modelo: String,
        imagen: String,
        comentarios: String,
        basico: String,
        otros: String,
        tecnico: String,
        aprobado: String,
        estado: String
    ) {
        val modelModel = listOf(
            id,
            data,
            type,
            marca,
            modelo,
            imagen,
            comentarios,
            basico,
            otros,
            tecnico,
            aprobado,
            estado
        )
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = updateModelUseCase(urlId, modelModel)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        updateModel.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        updateModel.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun deleteModel(urlId: urlId, id: String) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = deleteModelUseCase(urlId, id)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        deleteModel.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        deleteModel.postValue(Result.failure(ex))
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