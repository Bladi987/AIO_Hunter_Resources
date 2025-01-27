package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class deleteRegisterUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId, register: register): Result<String> {

        val responseResult = repository.deleteRegister(urlId, register)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Respuesta")
                Log.i("BladiDevDelete", data.toString())
                if (data != null) Result.success(data[5].asString) else Result.failure(Exception("Error"))
            }
            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }

    }

}