package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class deleteFileSheetUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, fileDetails: fileDetails): String {
        val response = repository.deleteFileSheet(urlid, fileDetails).asJsonObject
        Log.i("BladiDev1", response.toString())
        return if (response != null)
            response.get("Resultado")?.asString.toString()
        else "error"
    }
}