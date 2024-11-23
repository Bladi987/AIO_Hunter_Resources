package com.kasolution.aiohunterresources.data

import android.util.Log
import com.google.gson.JsonElement
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.recursoshunter.data.network.Service2

class RepositoryFichasTecnicas() {
    val log="BladiDevRepository"
    var idScript:String?=null
    var idSheet:String?=null
//    private val api = Service(idScript!!,idSheet!!)
    private var api:Service2?=null
    //general Function
//    private fun recuperarurl(ids:String){
//        var valor=ids.split("->")
//        idScript=valor[0]
//        idSheet=valor[1]
//        api= Service(idScript!!,idSheet!!)
//    }
    private fun procesarUrl(urlId: urlId){
        api= Service2(urlId)
    }

    //BrandModule
    suspend fun getBrand(urlId: urlId): JsonElement {
        procesarUrl(urlId)
        val response = api!!.getBrand()
        return response!!
    }

    suspend fun getModel(urlId: urlId,filter: String,column:Int): JsonElement {
        procesarUrl(urlId)
        val response = api!!.getModel(filter,column)
        return response!!
    }

    suspend fun insertModel(urlId: urlId,datos: List<String>): JsonElement {
        procesarUrl(urlId)
        val response = api!!.insertModel(datos)
        return response!!
    }
    suspend fun updateModel(urlId: urlId,datos: List<String>): JsonElement {
        procesarUrl(urlId)
        val response = api!!.updateModel(datos)
        return response!!
    }
    suspend fun deleteModel(urlId: urlId,id:String):JsonElement{
        procesarUrl(urlId)
        val response=api!!.deleteModel(id)
        return response!!
    }

//    //secureModule
//    suspend fun login(ids:String,user: String, password: String): JsonElement {
//        recuperarurl(ids)
//        val response = api!!.login(user, password)
//        return response!!
//    }
//
//    suspend fun getUser(ids:String): JsonElement {
//        recuperarurl(ids)
//        val response = api!!.getUser()
//        return response!!
//    }
//    suspend fun insertUser(ids:String,datos: List<String>): JsonElement {
//        recuperarurl(ids)
//        val response = api!!.insertUser(datos)
//        return response!!
//    }
//    suspend fun updateUser(ids:String,datos: List<String>): JsonElement {
//        recuperarurl(ids)
//        val response = api!!.updateUser(datos)
//        return response!!
//    }
//    suspend fun deleteUser(ids:String,id:String): JsonElement {
//        recuperarurl(ids)
//        val response = api!!.deleteUser(id)
//        return response!!
//    }
//    //settingModule
//    suspend fun getSettings(ids:String): JsonElement {
//        recuperarurl(ids)
//        val response = api!!.getSettings()
//        Log.i(log,response.toString())
//        return response!!
//    }
}