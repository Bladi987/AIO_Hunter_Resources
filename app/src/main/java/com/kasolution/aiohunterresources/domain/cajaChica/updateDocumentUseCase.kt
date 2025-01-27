package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.google.gson.JsonPrimitive
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class updateDocumentUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, file: file): Result<file> {
        lateinit var datafileSheet: file
        val responseResult = repository.updateDocument(urlid, file)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val resultado = response?.get("Resultado")
                if (resultado is JsonPrimitive && resultado.asString == "exito") {
                    val idFile = response.get("idFile").asString
                    val fileName = response.get("newName").asString
                    datafileSheet = file(idFile, fileName)
                }
                Result.success(datafileSheet)
            }

            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}