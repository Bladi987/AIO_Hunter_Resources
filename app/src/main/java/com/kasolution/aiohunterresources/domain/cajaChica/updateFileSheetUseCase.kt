package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.google.gson.JsonPrimitive
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class updateFileSheetUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(
        urlid: urlId,
        fileDetails: fileDetails,
        adicional: List<String>
    ): Result<fileDetails> {
        lateinit var datafileSheet: fileDetails
        val responseResult = repository.updateFileSheet(urlid, fileDetails, adicional)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val resultado = response?.get("Resultado")
                if (resultado is JsonPrimitive && resultado.asString == "error") {
                    datafileSheet = fileDetails("", "")
                } else {
                    val resultadoObj = resultado?.asJsonObject
                    if (resultadoObj != null && resultadoObj.has("sheet") && resultadoObj.has("nameTecnico")) {
                        val sheet = resultadoObj.get("newName").asString
                        datafileSheet = fileDetails(sheet, sheet)
                    } else {
                        datafileSheet = fileDetails("", "")
                    }
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