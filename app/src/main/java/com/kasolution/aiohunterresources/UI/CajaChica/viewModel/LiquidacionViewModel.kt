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
    val getLiquidacion = MutableLiveData<Result<ArrayList<liquidacion>>>()
    val insertLiquidacion = MutableLiveData<Result<liquidacion>>()
    val updateLiquidacion = MutableLiveData<Result<liquidacion>>()
    val deleteLiquidacion = MutableLiveData<Result<String>>()
    val getResumenGastos = MutableLiveData<Result<String>>()
    val exception = MutableLiveData<String>()

    val isloading = MutableLiveData<Boolean>()
    var getLiquidacionUseCase = getLiquidacionUseCase()
    var insertLiquidacionUseCase = insertLiquidacionUseCase()
    var updateLiquidacionUseCase = updateLiquidacionUseCase()
    var deleteLiquidacionUseCase = deleteLiquidacionUseCase()
    var getSaldoContableUseCase = getResumenGastosUseCase()

    fun getLiquidacion(urlid: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getLiquidacionUseCase(urlid)
                if (response.isSuccess) {
                    response.getOrNull()?.let { lista ->
                        getLiquidacion.postValue(Result.success(lista))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        getLiquidacion.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun onRefresh(urlid: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getLiquidacionUseCase(urlid)
                if (response.isSuccess) {
                    response.getOrNull()?.let { lista ->
                        getLiquidacion.postValue(Result.success(lista))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        getLiquidacion.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun insertLiquidacion(urlid: urlId, liquidacion: liquidacion, adicional: ArrayList<Int>) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = insertLiquidacionUseCase(urlid, liquidacion, adicional)
                if (response.isSuccess) {
                    response.getOrNull()?.let { liquidacion ->
                        insertLiquidacion.postValue(Result.success(liquidacion))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        getLiquidacion.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun updateLiquidacion(urlid: urlId, liquidacion: liquidacion) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = updateLiquidacionUseCase(urlid, liquidacion)
                if (response.isSuccess) {
                    response.getOrNull()?.let { liquidacion ->
                        updateLiquidacion.postValue(Result.success(liquidacion))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        getLiquidacion.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun deleteLiquidacion(urlid: urlId, liquidacion: liquidacion) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = deleteLiquidacionUseCase(urlid, liquidacion)
                if (response.isSuccess) {
                    response.getOrNull()?.let { liquidacion ->
                        deleteLiquidacion.postValue(Result.success(liquidacion))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        getLiquidacion.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {

            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun getResumenGastos(urlid: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getSaldoContableUseCase(urlid)
                if (response.isSuccess) {
                    response.getOrNull()?.let { liquidacion ->
                        getResumenGastos.postValue(Result.success(liquidacion))
                    }
                }else{
                    response.exceptionOrNull()?.let { ex ->
                        getLiquidacion.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            }catch (e: Exception){
                exception.postValue(e.message)
            }finally {
                isloading.postValue(false)
            }
        }
    }
}