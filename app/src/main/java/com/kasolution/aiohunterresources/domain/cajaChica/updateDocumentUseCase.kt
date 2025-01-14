package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.google.gson.JsonPrimitive
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class updateDocumentUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, file: file): file {
        lateinit var datafileSheet: file
        val response = repository.updateDocument(urlid, file).asJsonObject
        Log.i("BladiDevMonitor", response.toString())
        val resultado = response?.get("Resultado")
        if (resultado is JsonPrimitive && resultado.asString == "exito") {
            val idFile = response.get("idFile").asString
            val fileName = response.get("newName").asString
            datafileSheet = file(idFile, fileName)
        } else {

        }
        return datafileSheet
    }
}