package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class deleteLiquidacionUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId, liquidacion: liquidacion): String {
        lateinit var responseId: String
        val response = repository.deleteLiquidacion(urlid, liquidacion).asJsonObject
        Log.i("BladiDev1", response.toString())
        if (response != null) responseId = "done"
        else responseId = "error"
        return responseId
    }

}