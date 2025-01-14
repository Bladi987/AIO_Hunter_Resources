package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class deleteDocumentUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, file: file): String {
        val response = repository.deleteDocument(urlid, file).asJsonObject
        Log.i("BladiDev1", response.toString())
        return if (response != null)
            response.get("Resultado")?.asString.toString()
        else "error"
    }
}