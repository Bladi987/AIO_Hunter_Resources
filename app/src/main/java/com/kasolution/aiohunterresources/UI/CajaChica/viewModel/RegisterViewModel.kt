package com.kasolution.aiohunterresources.UI.CajaChica.viewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.cajaChica.deleteRegisterUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.getRegisterUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.insertRegisterUseCase
import com.kasolution.aiohunterresources.domain.cajaChica.updateRegisterUseCase
import kotlinx.coroutines.launch


class RegisterViewModel : ViewModel() {
    private val _resumen = MutableLiveData<Map<String, Pair<Int, Double>>>()
    val resumen: LiveData<Map<String, Pair<Int, Double>>> get() = _resumen
    val exception = MutableLiveData<String>()
    val isloading = MutableLiveData<Boolean>()
    val getRegister = MutableLiveData<Result<ArrayList<register>>>()
    val insertarRegister = MutableLiveData<Result<register>>()
    val updateRegister = MutableLiveData<Result<register>>()
    val deleteRegister = MutableLiveData<Result<String>>()
    var getRegisterUseCase = getRegisterUseCase()
    var insertRegisterUseCase = insertRegisterUseCase()
    var updateRegisterUseCase = updateRegisterUseCase()
    var deleteRegisterUseCase = deleteRegisterUseCase()

    private var listRegister = ArrayList<register>() // Lista interna

    fun onCreate(urlId: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getRegisterUseCase(urlId)
                if (response.isSuccess) {
                    response.getOrNull()?.let { lista ->
                        listRegister = ArrayList(lista)
                        getRegister.postValue(Result.success(listRegister))
                        calcularTotales(listRegister)
                    }
                } else {
                    // Si el resultado tiene una excepción, se maneja aquí
                    response.exceptionOrNull()?.let { ex ->
                        getRegister.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun onRefresh(urlId: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getRegisterUseCase(urlId)
                if (response.isSuccess) {
                    response.getOrNull()?.let { lista ->
                        listRegister = ArrayList(lista)
                        getRegister.postValue(Result.success(listRegister))
                        calcularTotales(listRegister)
                    }
                } else {
                    // Si el resultado tiene una excepción, se maneja aquí
                    response.exceptionOrNull()?.let { ex ->
                        getRegister.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun insertRegister(urlId: urlId, register: register) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = insertRegisterUseCase(urlId, register)
                if (response.isSuccess) {
                    // Extraemos el registro desde el resultado exitoso
                    response.getOrNull()?.let { registro ->
                        listRegister.add(0, registro) // Agregar al inicio de la lista
                        insertarRegister.postValue(Result.success(registro)) // Notificar inserción
                        calcularTotales(listRegister) // Actualizar resumen
                    }
                } else {
                    // Si el resultado tiene una excepción, se maneja aquí
                    response.exceptionOrNull()?.let { ex ->
                        insertarRegister.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun updateRegister(urlId: urlId, register: register) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = updateRegisterUseCase(urlId, register)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        val index = listRegister.indexOfFirst { it.id == register.id }
                        if (index != -1) {
                            listRegister[index] = registro // Actualizar registro
                            updateRegister.postValue(Result.success(registro)) // Notificar actualización
                            calcularTotales(listRegister) // Actualizar resumen
                        }
                    }
                } else {
                    // Si el resultado tiene una excepción, se maneja aquí
                    response.exceptionOrNull()?.let { ex ->
                        updateRegister.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun deleteRegister(urlId: urlId, register: register) {
        viewModelScope.launch {
            isloading.postValue(true)
            try {
                val response = deleteRegisterUseCase(urlId, register)
                if (response.isSuccess) {
                    response.getOrNull()?.let { respuesta ->
                        val index = listRegister.indexOfFirst { it.id == register.id }
                        if (index != -1) {
                            listRegister.removeAt(index) // Eliminar registro
                            deleteRegister.postValue(Result.success(respuesta))
                            calcularTotales(listRegister) // Actualizar resumen
                        }
                    }
                } else {
                    // Si el resultado tiene una excepción, se maneja aquí
                    response.exceptionOrNull()?.let { ex ->
                        updateRegister.postValue(Result.failure(ex)) // Publicamos el error como Result.failure
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    private fun calcularTotales(response: ArrayList<register>? = null) {
        if (response != null) {
            val resumenResultados = mutableMapOf(
                "c_movilidad" to Pair(0, 0.0),
                "c_alimentacion" to Pair(0, 0.0),
                "c_alojamiento" to Pair(0, 0.0),
                "c_otros" to Pair(0, 0.0),
                "s_movilidad" to Pair(0, 0.0),
                "s_alimentacion" to Pair(0, 0.0),
                "s_alojamiento" to Pair(0, 0.0),
                "s_otros" to Pair(0, 0.0),
                "total_con_sustento" to Pair(0, 0.0),
                "total_sin_sustento" to Pair(0, 0.0)
            )

            // Variables para almacenar totales
            var totalRegistros = 0
            var totalSuma = 0.0

            // Función auxiliar para agregar al resumen
            fun agregarAlResumen(campo: String, monto: String?, conSustento: Boolean) {
                if (!monto.isNullOrEmpty()) {
                    val montoNumerico =
                        monto.replace("S/", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
                    if (montoNumerico > 0) {
                        val (cantidad, suma) = resumenResultados[campo] ?: Pair(0, 0.0)
                        resumenResultados[campo] = Pair(cantidad + 1, suma + montoNumerico)

                        // Acumular totales
                        totalRegistros++
                        totalSuma += montoNumerico
                    }
                }
            }

            // Recorrer cada registro y contar/sumar los montos
            for (registro in response) {
                // Asumimos que c_ son con sustento y s_ son sin sustento
                agregarAlResumen("c_movilidad", registro.c_movilidad, true)
                agregarAlResumen("c_alimentacion", registro.c_alimentacion, true)
                agregarAlResumen("c_alojamiento", registro.c_alojamiento, true)
                agregarAlResumen("c_otros", registro.c_otros, true)
                agregarAlResumen("s_movilidad", registro.s_movilidad, false)
                agregarAlResumen("s_alimentacion", registro.s_alimentacion, false)
                agregarAlResumen("s_alojamiento", registro.s_alojamiento, false)
                agregarAlResumen("s_otros", registro.s_otros, false)
            }
            // Agregar totales al resumen
            resumenResultados["Total Registros"] = Pair(totalRegistros, 0.0)
            resumenResultados["Total Suma"] = Pair(0, totalSuma)

            _resumen.value = resumenResultados // Actualiza el LiveData
        }
    }
}