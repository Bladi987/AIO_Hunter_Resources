package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class updateLiquidacionUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, liquidacion: liquidacion): Result<liquidacion> {
        lateinit var dataliquidacion: liquidacion
        val responseResult = repository.updateLiquidacion(urlid, liquidacion)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Respuesta")
                if (data != null) {
                    dataliquidacion = liquidacion(
                        data[0].asString,
                        data[1].asString,
                        data[2].asString,
                        data[3].asString,
                        data[4].asString,
                        data[5].asString
                    )
                } else {
                    dataliquidacion = liquidacion("", "", "", "", "", "")
                }
                Result.success(dataliquidacion)
            }

            else -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }

        }
    }
}