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
    ): fileDetails {
        lateinit var datafileSheet: fileDetails
        val response = repository.updateFileSheet(urlid, fileDetails, adicional).asJsonObject
        Log.i("BladiDevMonitor", response.toString())
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
        return datafileSheet
    }

}