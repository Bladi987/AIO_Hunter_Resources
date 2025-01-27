package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class createDocumentUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, file: file, adicional: List<String>): Result<file> {
        lateinit var dataFileDocument: file
        val responseResult = repository.createDocument(urlid, file, adicional)
        return when (responseResult.isSuccess) {
            true -> {
                // Obtener el valor de "Resultado"
                val response = responseResult.getOrNull()?.asJsonObject
                val respuesta = response?.get("Resultado")?.asString
                // Verificar si "Resultado" es una cadena (como "duplicado")
                if (respuesta == "exito") {
                    // la respuesta es exitoso accedemos al resto de datos
                    val idFile = response.get("idFile").asString
                    val fileName = response.get("FileName").asString
                    dataFileDocument = file(idFile, fileName)
                } else {
                    dataFileDocument = file("", "")
                }
                Result.success(dataFileDocument)
            }
            false->{
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }

    }
}