package com.kasolution.aiohunterresources.UI.CajaChica.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.cajaChica.deleteLiquidacionUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.getLiquidacionUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.getResumenGastosUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.insertLiquidacionUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.updateLiquidacionUseCase
import kotlinx.coroutines.launch

class LiquidacionViewModel : ViewModel() {
    val getLiquidacion = MutableLiveData<ArrayList<liquidacion>>()
    val insertLiquidacion = MutableLiveData<liquidacion>()
    val updateLiquidacion = MutableLiveData<liquidacion>()
    val deleteLiquidacion = MutableLiveData<String>()
    val getResumenGastos = MutableLiveData<String>()

    //    val reset=MutableLiveData<Boolean>()
    val isloading = MutableLiveData<Boolean>()
    var getLiquidacionUseCase = getLiquidacionUseCase()
    var insertLiquidacionUseCase = insertLiquidacionUseCase()
    var updateLiquidacionUseCase = updateLiquidacionUseCase()
    var deleteLiquidacionUseCase = deleteLiquidacionUseCase()
    var getSaldoContableUseCase = getResumenGastosUseCase()

    fun getLiquidacion(urlid: urlId) {
        if (getLiquidacion.value.isNullOrEmpty()) {
            viewModelScope.launch {
                isloading.postValue(true)
                val response = getLiquidacionUseCase(urlid)
                if (response.isNotEmpty()) {
                    getLiquidacion.postValue(response)
                }
                isloading.postValue(false)
            }
        }
    }

    fun onRefresh(urlid: urlId) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = getLiquidacionUseCase(urlid)
            if (response.isNotEmpty()) {
                getLiquidacion.postValue(response)
            }
            isloading.postValue(false)
        }
    }

    fun insertLiquidacion(urlid: urlId, liquidacion: liquidacion) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = insertLiquidacionUseCase(urlid, liquidacion)
            insertLiquidacion.postValue(response)
            isloading.postValue(false)
        }
    }

    fun updateLiquidacion(urlid: urlId, liquidacion: liquidacion) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = updateLiquidacionUseCase(urlid, liquidacion)
            updateLiquidacion.postValue(response)
            isloading.postValue(false)
        }
    }

    fun deleteLiquidacion(urlid: urlId, liquidacion: liquidacion) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = deleteLiquidacionUseCase(urlid, liquidacion)
            deleteLiquidacion.postValue(response)
            isloading.postValue(false)
        }
    }

    fun getResumenGastos(urlid: urlId) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = getSaldoContableUseCase(urlid)
            if (response.isNotEmpty()) {
                getResumenGastos.postValue(response)
            }
            isloading.postValue(false)
        }
    }
}