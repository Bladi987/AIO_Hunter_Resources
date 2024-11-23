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
    val vehicleModel = MutableLiveData<ArrayList<VehicleModel>>()
    val insertarModel = MutableLiveData<VehicleModel>()
    val updateModel = MutableLiveData<VehicleModel>()
    val deleteModel = MutableLiveData<String>()
    val isloading = MutableLiveData<Boolean>()
    var getModeloUseCase = getModelUseCase()
    var insertModelUseCase = insertModelUseCase()
    var updateModelUseCase = updateModelUseCase()
    var deleteModelUseCase = deleteModelUseCase()

    fun onCreate(urlId: urlId, filter: String, state: String) {
        if (vehicleModel.value.isNullOrEmpty()) {
            viewModelScope.launch {
                isloading.postValue(true)
                val response = getModeloUseCase(urlId, filter, 1, state)
                if (response.isNotEmpty()) {
                    if (response.size == 1) {
                        if (response[0].id.isNotEmpty())
                            vehicleModel.postValue(response)
                    } else {
                        vehicleModel.postValue(response)
                    }
                }
                isloading.postValue(false)
            }
        }
    }

    fun onRefresh(urlId: urlId, filter: String, state: String) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = getModeloUseCase(urlId, filter, 1, state)
            if (response.isNotEmpty()) {
                if (response.size == 1) {
                    if (response[0].id.isNotEmpty())
                        vehicleModel.postValue(response)
                } else {
                    vehicleModel.postValue(response)
                }
            }
            isloading.postValue(false)
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
            isloading.postValue(true)
            val response = insertModelUseCase(urlId, listaModel)
            insertarModel.postValue(response)
            isloading.postValue(false)
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
            isloading.postValue(true)
            val response = updateModelUseCase(urlId, modelModel)
            updateModel.postValue(response)
            isloading.postValue(false)
        }
    }

    fun deleteModel(urlId: urlId, id: String) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = deleteModelUseCase(urlId, id)
            deleteModel.postValue(response)
            isloading.postValue(false)
        }
    }
}