package com.kasolution.aiohunterresources.data

import android.util.Log
import com.google.gson.JsonElement
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.recursoshunter.data.network.Service
import com.kasolution.recursoshunter.data.network.Service2

class RepositoryCajaChica() {
    val log="BladiDevRepository"
    var idScript:String?=null
    var idSheet:String?=null
    private var api:Service2?=null

    private fun procesarUrl(urlId: urlId){
        api=Service2(urlId)
    }

    //BrandModule
    suspend fun getFile(urlId: urlId): JsonElement {
        procesarUrl(urlId)
        val response = api!!.getFile()
        return response!!
    }
    suspend fun getDetailsFile(urlId: urlId): JsonElement {

        procesarUrl(urlId)
        val response = api!!.getDetailsFile()
        return response!!
    }

    suspend fun getRegister(urlId: urlId): JsonElement {
        procesarUrl(urlId)
        val response = api!!.getRegister()
        return response!!
    }
    suspend fun insertRegister(urlId: urlId,register: register): JsonElement {
        procesarUrl(urlId)
        val response = api!!.insertRegister(register)
        return response!!
    }
    suspend fun updateRegister(urlId: urlId,register: register): JsonElement {
        procesarUrl(urlId)
        val response = api!!.updateRegister(register)
        return response!!
    }
    suspend fun deleteRegister(urlId: urlId,register: register): JsonElement {
        procesarUrl(urlId)
        val response = api!!.deleteRegister(register)
        return response!!
    }
    suspend fun getLiquidacion(urlId: urlId): JsonElement {
        procesarUrl(urlId)
        val response = api!!.getLiquidacion()
        return response!!
    }
    suspend fun insertLiquidacion(urlid: urlId, liquidacion: liquidacion): JsonElement {
        procesarUrl(urlid)
        val response = api!!.insertLiquidacion(liquidacion)
        return response!!
    }
    suspend fun updateLiquidacion(urlid: urlId, liquidacion: liquidacion): JsonElement {
        procesarUrl(urlid)
        val response = api!!.updateLiquidacion(liquidacion)
        return response!!
    }
    suspend fun deleteLiquidacion(urlid: urlId, liquidacion: liquidacion): JsonElement {
        procesarUrl(urlid)
        val response = api!!.deleteLiquidacion(liquidacion)
        return response!!
    }
    suspend fun getResumenGastos(urlId: urlId): JsonElement {
        procesarUrl(urlId)
        val response = api!!.getResumenGastos()
        return response!!
    }
}