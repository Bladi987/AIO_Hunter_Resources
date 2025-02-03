package com.kasolution.aiohunterresources.data


import com.google.gson.JsonElement
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.recursoshunter.data.network.Service

class RepositoryCajaChica() {
    val log="BladiDevRepository"
    var idScript:String?=null
    var idSheet:String?=null
    private var api:Service?=null

    private fun procesarUrl(urlId: urlId){
        api=Service(urlId)
    }

    //BrandModule
    suspend fun getFile(urlId: urlId): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.getFile()
        return response
    }
    suspend fun createDocument(urlId: urlId, file: file, adicional:List<String>): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.createDocument(file,adicional)
        return response
    }
    suspend fun updateDocument(urlId: urlId,file: file): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.updateDocument(file)
        return response
    }
    suspend fun deleteDocument(urlId: urlId,file: file): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.deleteDocument(file)
        return response
    }
    suspend fun getDetailsFile(urlId: urlId): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.getDetailsFile()
        return response
    }
    suspend fun insertFileSheet(urlId: urlId,fileDetails: fileDetails,adicional:List<String>): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.insertFileSheet(fileDetails,adicional)
        return response
    }
    suspend fun updateFileSheet(urlId: urlId,fileDetails: fileDetails,adicional: List<String>): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.updateFileSheet(fileDetails,adicional)
        return response
    }
    suspend fun deleteFileSheet(urlId: urlId,fileDetails: fileDetails): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.deleteFileSheet(fileDetails)
        return response
    }
    suspend fun getRegister(urlId: urlId): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.getRegister()
        return response
    }
    suspend fun insertRegister(urlId: urlId,register: register): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.insertRegister(register)
        return response
    }
    suspend fun updateRegister(urlId: urlId,register: register): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.updateRegister(register)
        return response
    }
    suspend fun deleteRegister(urlId: urlId,register: register): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.deleteRegister(register)
        return response
    }
    suspend fun getLiquidacion(urlId: urlId): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.getLiquidacion()
        return response
    }
    suspend fun insertLiquidacion(urlid: urlId, liquidacion: liquidacion,adicional:ArrayList<Int>): Result<JsonElement?> {
        procesarUrl(urlid)
        val response = api!!.insertLiquidacion(liquidacion,adicional)
        return response
    }
    suspend fun updateLiquidacion(urlid: urlId, liquidacion: liquidacion): Result<JsonElement?> {
        procesarUrl(urlid)
        val response = api!!.updateLiquidacion(liquidacion)
        return response
    }
    suspend fun deleteLiquidacion(urlid: urlId, liquidacion: liquidacion): Result<JsonElement?> {
        procesarUrl(urlid)
        val response = api!!.deleteLiquidacion(liquidacion)
        return response
    }
    suspend fun getDownloadLink(urlid: urlId): Result<JsonElement?> {
        procesarUrl(urlid)
        val response = api!!.getDownloadLink()
        return response
    }
    suspend fun getResumenGastos(urlId: urlId): Result<JsonElement?> {
        procesarUrl(urlId)
        val response = api!!.getResumenGastos()
        return response
    }
}