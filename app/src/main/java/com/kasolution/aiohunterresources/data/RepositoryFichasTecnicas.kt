package com.kasolution.aiohunterresources.data

import com.google.gson.JsonElement
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.recursoshunter.data.network.Service

class RepositoryFichasTecnicas() {
    val log = "BladiDevRepository"
    var idScript: String? = null
    var idSheet: String? = null

    private var api: Service? = null

    //general Function
    private fun procesarUrl(urlId: urlId) {
        api = Service(urlId)
    }

    //BrandModule
    suspend fun getBrand(urlId: urlId): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.getBrand()
        return response
    }

    suspend fun getModel(urlId: urlId, filter: String, column: Int): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.getModel(filter, column)
        return response
    }

    suspend fun insertModel(urlId: urlId, datos: List<String>): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.insertModel(datos)
        return response
    }

    suspend fun updateModel(urlId: urlId, datos: List<String>): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.updateModel(datos)
        return response
    }

    suspend fun deleteModel(urlId: urlId, id: String): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.deleteModel(id)
        return response
    }
}