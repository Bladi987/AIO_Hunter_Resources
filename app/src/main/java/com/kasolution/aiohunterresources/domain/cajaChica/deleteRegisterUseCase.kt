package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class deleteRegisterUseCase() {
    private val repository= RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId,register: register):String{
        lateinit var respuesta: String
        val response=repository.deleteRegister(urlId,register).asJsonObject

        Log.i("deleteRegisterUseCase",response.toString())
        val data = response?.getAsJsonArray("Resultado")
        if(data!=null)
            respuesta="done"
        else respuesta="error"
        return respuesta
    }

}