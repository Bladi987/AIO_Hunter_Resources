package com.kasolution.aiohunterresources.UI.ControlEquipos.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.model.equipos
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.controlEquipos.getEquiposUseCase
import com.kasolution.aiohunterresources.domain.controlEquipos.updateEquipoUseCase
import kotlinx.coroutines.launch

class equiposViewModel : ViewModel() {
    var isDataLoaded = false
    val exception = MutableLiveData<String>()
    val listEquipos = MutableLiveData<Result<ArrayList<equipos>>>()
    val updateEquipos = MutableLiveData<Result<equipos>>()
    val isloading = MutableLiveData<Boolean>()
    var getEquiposUseCase = getEquiposUseCase()
    var updateEquiposUseCase = updateEquipoUseCase()

    fun getEquipos(urlid: urlId,user:String) {
        if (!isDataLoaded) {
            viewModelScope.launch {
                try {
                    isloading.postValue(true)
                    val response = getEquiposUseCase(urlid,user)
                    if (response.isSuccess) {
                        response.getOrNull()?.let { lista ->
                            listEquipos.postValue(Result.success(lista))
                            isDataLoaded = true
                        }
                    } else {
                        response.exceptionOrNull()?.let { ex ->
                            listEquipos.postValue(Result.failure(ex))
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

    fun onRefresh(urlid: urlId,user:String) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getEquiposUseCase(urlid,user)
                if (response.isSuccess) {
                    response.getOrNull()?.let { lista ->
                        listEquipos.postValue(Result.success(lista))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        listEquipos.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }


    fun updateEquipos(urlid: urlId, equipos: equipos) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = updateEquiposUseCase(urlid, equipos)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        updateEquipos.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        updateEquipos.postValue(Result.failure(ex))
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