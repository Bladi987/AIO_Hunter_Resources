package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.google.gson.JsonPrimitive
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class insertFileSheetUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(
        urlid: urlId,
        fileDetails: fileDetails,
        adicional: List<String>
    ): Result<fileDetails> {
        lateinit var dataFileSheet: fileDetails
        val responseResult = repository.insertFileSheet(urlid, fileDetails, adicional)
        return when (responseResult.isSuccess) {
            true -> {
                // Obtener el valor de "Resultado"
                val response = responseResult.getOrNull()?.asJsonObject
                val resultado = response?.get("Resultado")

                // Verificar si "Resultado" es una cadena (como "duplicado")
                if (resultado is JsonPrimitive && resultado.asString == "duplicado") {
                    // Si es "duplicado", devolver un fileDetails vacío
                    dataFileSheet = fileDetails("", "")
                } else {
                    // Caso en que "Resultado" es un objeto (con los detalles de la hoja)
                    val resultadoObj = resultado?.asJsonObject
                    if (resultadoObj != null && resultadoObj.has("sheet") && resultadoObj.has("nameTecnico")) {
                        val sheet = resultadoObj.get("sheet").asString

                        // Crear el objeto dataFileSheet con los datos recibidos
                        dataFileSheet = fileDetails(sheet, sheet)
                    } else {
                        // Si no se encuentran los datos esperados, devolver un fileDetails vacío
                        dataFileSheet = fileDetails("", "")
                    }
                }
                Result.success(dataFileSheet)
            }

            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}