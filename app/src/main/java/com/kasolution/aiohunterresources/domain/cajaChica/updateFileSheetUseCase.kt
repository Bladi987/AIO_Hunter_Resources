package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class updateFileSheetUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, fileDetails: fileDetails): fileDetails {
        lateinit var datafileSheet: fileDetails
        val response = repository.updateFileSheet(urlid, fileDetails).asJsonObject
        Log.i("BladiDevMonitor", response.toString())
        val data = response?.get("Resultado")?.asString
        if (data != null) {
            datafileSheet = fileDetails(
                data.toString(),
                data.toString()
            )
        } else {
            datafileSheet = fileDetails("", "")
        }
        return datafileSheet
    }

}