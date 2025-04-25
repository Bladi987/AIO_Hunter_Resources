package com.kasolution.aiohunterresources.data

import com.google.gson.JsonElement
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.model.equipos
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.recursoshunter.data.network.Service

class RepositoryControlEquipos() {
    var idScript: String? = null
    var idSheet: String? = null

    private var api: Service? = null

    //general Function
    private fun procesarUrl(urlId: urlId) {
        api = Service(urlId)
    }

    //BrandModule
    suspend fun getEquipos(urlId: urlId,user:String): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.getEquipos(user)
        return response
    }

    suspend fun updateEquipo(urlId: urlId, equipo:equipos): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.updateEquipo(equipo)
        return response
    }

}