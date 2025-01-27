package com.kasolution.aiohunterresources.domain.cajaChica


import android.util.Log
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class getResumenGastosUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId): Result<String> {
        lateinit var saldoContable: String
        val responseResult = repository.getResumenGastos(urlId)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.get("Resultado")
                if (data != null) Result.success(data.asString)
                else Result.success("0")
            }

            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }

    }
}