package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class deleteLiquidacionUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, liquidacion: liquidacion): Result<String> {
        lateinit var responseId: String
        val responseResult = repository.deleteLiquidacion(urlid, liquidacion)
        return when (responseResult.isSuccess) {
            true -> {
                Result.success("done")
            }
            else -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}