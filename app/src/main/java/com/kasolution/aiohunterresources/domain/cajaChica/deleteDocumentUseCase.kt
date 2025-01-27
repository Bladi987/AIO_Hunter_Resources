package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class deleteDocumentUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, file: file): Result<String> {
        val responseResult = repository.deleteDocument(urlid, file)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                if (response != null) Result.success(response.get("Resultado").asString.toString())
                else Result.failure(Exception("Error"))
            }
            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}