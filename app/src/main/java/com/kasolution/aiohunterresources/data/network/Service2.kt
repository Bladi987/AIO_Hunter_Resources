package com.kasolution.recursoshunter.data.network

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.RetrofitHelper
import com.kasolution.aiohunterresources.core.dataConexion.urlId

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Service2(urlId: urlId) {
    val log = "BladiDevService"

    private val urlScript =
        "https://script.google.com/macros/s/${urlId.idScript}/" //este sera la url para la script
    private val idFile = urlId.idFile
    private val sheetName = urlId.sheetName
    private val idSheet = urlId.idSheet

    //privateModule
    private val retrofit = RetrofitHelper.getRetrofit(urlScript)


    //brandModule
    suspend fun getBrand(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getBrand")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "BRAND")


        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getModel(filter: String, column: Int): JsonElement? {
        val valueAction: String
        if (column == 1) {
            valueAction = "getData"
        } else if (column == 2) {
            valueAction = "getDataModel"
        } else {
            valueAction = "getDataModelApprove"
        }

        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", valueAction)
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "VEHICLEDATA")
        jsonRequest.addProperty("filter", filter)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun insertModel(data: List<String>): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "add")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "VEHICLEDATA")
        val rowArray = JsonArray()
        rowArray.add(data[0])
        rowArray.add(data[1])
        rowArray.add(data[2])
        rowArray.add(data[3])
        rowArray.add(data[4])
        rowArray.add(data[5])
        rowArray.add(data[6])
        rowArray.add(data[7])
        rowArray.add(data[8])
        rowArray.add(data[9])
        jsonRequest.add("rows", rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun updateModel(data: List<String>): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "editModel")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "VEHICLEDATA")
        val rowArray = JsonArray()
        rowArray.add(data[0])
        rowArray.add(data[1])
        rowArray.add(data[2])
        rowArray.add(data[3])
        rowArray.add(data[4])
        rowArray.add(data[5])
        rowArray.add(data[6])
        rowArray.add(data[7])
        rowArray.add(data[8])
        rowArray.add(data[9])
        rowArray.add(data[10])
        rowArray.add(data[11])
        jsonRequest.add("rows", rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun deleteModel(id: String): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteModel")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "VEHICLEDATA")
        jsonRequest.addProperty("id", id)
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }


    //secureModule
    suspend fun login(user: user): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "login")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "User")

        jsonRequest.addProperty("user", user.user)
        jsonRequest.addProperty("password", user.password)
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getUser(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "readUsers")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "User")


        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun insertUser(user: user): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "addUser")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "User")
        val rowArray = JsonArray()
        rowArray.add(user.name)
        rowArray.add(user.lastName)
        rowArray.add(user.user)
        rowArray.add(user.password)
        rowArray.add(user.tipo)
        jsonRequest.add("rows", rowArray)
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun updateUser(user: user): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "editUser")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "User")
        val rowArray = JsonArray()
        rowArray.add(user.id)
        rowArray.add(user.name)
        rowArray.add(user.lastName)
        rowArray.add(user.user)
        rowArray.add(user.password)
        rowArray.add(user.tipo)
        jsonRequest.add("rows", rowArray)
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun deleteUser(user: user): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteUser")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "User")
        jsonRequest.addProperty("id", user.id)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }
    //settingsModule

    suspend fun getSettings(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getSettings")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "Settings")
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }


    //module Caja Chica
    suspend fun getFile(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getFiles")
        jsonRequest.addProperty("idFile", idFile)
        Log.i("BladiDev", jsonRequest.toString())
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun createDocument(file: file, adicional: List<String>): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "newDocument")
        jsonRequest.addProperty("idFile", idFile)
        jsonRequest.addProperty("nameDocument", file.nombre)
        jsonRequest.addProperty("nameFirstSheet", adicional[0])
        jsonRequest.addProperty("nameTecnico", adicional[1])
        jsonRequest.addProperty("destino", adicional[2])
        jsonRequest.addProperty("fecha", adicional[3])
        Log.i("BladiDev", jsonRequest.toString())
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun updateDocument(file: file): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "updateDocument")
        jsonRequest.addProperty("idFile", file.id)
        jsonRequest.addProperty("newName", file.nombre)
        Log.i("BladiDev", jsonRequest.toString())
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun deleteDocument(file: file): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteDocument")
        jsonRequest.addProperty("idFile", file.id)
        Log.i("BladiDev", jsonRequest.toString())
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getDetailsFile(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getSheet")
        jsonRequest.addProperty("idSheet", idSheet)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun insertFileSheet(fileDetails: fileDetails, adicional: List<String>): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "newSheet")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", fileDetails.nombre)
        jsonRequest.addProperty("nameTecnico", adicional[0])
        jsonRequest.addProperty("destino", adicional[1])
        jsonRequest.addProperty("fecha", adicional[2])
        Log.i("BladiDev", jsonRequest.toString())
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun updateFileSheet(fileDetails: fileDetails, adicional: List<String>): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "updateSheet")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", fileDetails.nombre)
        jsonRequest.addProperty("newName", fileDetails.nombreReal)
        jsonRequest.addProperty("nameTecnico", adicional[0])
        jsonRequest.addProperty("destino", adicional[1])
        jsonRequest.addProperty("fecha", adicional[2])
        Log.i("BladiDev", jsonRequest.toString())
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun deleteFileSheet(fileDetails: fileDetails): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteSheet")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", fileDetails.nombreReal)
        Log.i("BladiDev", jsonRequest.toString())
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getRegister(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getData")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", sheetName)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun insertRegister(registro: register): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "addRegister")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", sheetName)

        val rowArray = JsonArray()
        val row1 = JsonArray()
        row1.add(registro.fecha)     //fecha
        row1.add(registro.ciudad)    //ciudad
        row1.add(registro.tipoDoc) //tipo de documento
        row1.add(registro.nroDoc) //nro documento
        row1.add(registro.proveedor) //proveedor
        row1.add(registro.descripcion) //descripcion

        //zona con sustento
        row1.add(registro.c_movilidad)     // cs-movilidad
        row1.add(registro.c_alimentacion)  //cs-alimentacionc_alimentacion)  //cs-alimentacion
        row1.add(registro.c_alojamiento)    //cs-alojamiento
        row1.add(registro.c_otros)    //cs-otros
        //zona sin sustento
        row1.add(registro.s_movilidad)    //ss-movildiad
        row1.add(registro.s_alimentacion)    //cs-alimentacion
        row1.add(registro.s_alojamiento)    //cs-alojamiento
        row1.add(registro.s_otros)    //cs-otros

        rowArray.add(row1)
        jsonRequest.add("rows", rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun updateRegister(registro: register): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "updateRegister")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", sheetName)

        val rowArray = JsonArray()
        val row1 = JsonArray()
        row1.add(registro.id)         //se envia el id el cual sera el index dentro del sheet
        row1.add(registro.fecha)     //fecha
        row1.add(registro.ciudad)    //ciudad
        row1.add(registro.tipoDoc) //tipo de documento
        row1.add(registro.nroDoc) //nro documento
        row1.add(registro.proveedor) //proveedor
        row1.add(registro.descripcion) //descripcion
        //zona con sustento
        row1.add(registro.c_movilidad)     // cs-movilidad
        row1.add(registro.c_alimentacion)  //cs-alimentacionc_alimentacion)  //cs-alimentacion
        row1.add(registro.c_alojamiento)    //cs-alojamiento
        row1.add(registro.c_otros)    //cs-otros
        //zona sin sustento
        row1.add(registro.s_movilidad)    //ss-movildiad
        row1.add(registro.s_alimentacion)    //cs-alimentacion
        row1.add(registro.s_alojamiento)    //cs-alojamiento
        row1.add(registro.s_otros)    //cs-otros

        rowArray.add(row1)
        jsonRequest.add("rows", rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun deleteRegister(registro: register): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteRegister")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", sheetName)

        val rowArray = JsonArray()
        val row1 = JsonArray()
        row1.add(registro.id)         //se envia el id el cual sera el index dentro del sheet
        row1.add(registro.fecha)     //fecha
        row1.add(registro.ciudad)    //ciudad
        row1.add(registro.tipoDoc) //tipo de documento
        row1.add(registro.nroDoc) //nro documento
        row1.add(registro.proveedor) //proveedor
        row1.add(registro.descripcion) //descripcion

        rowArray.add(row1)
        jsonRequest.add("rows", rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getLiquidacion(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getLiquidacion")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "LIQUIDACIONES")
        Log.i("BladiDev", "Datos enviados son: $jsonRequest")
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun insertLiquidacion(liquidacion: liquidacion): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "insertLiquidacion")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "LIQUIDACIONES")
        val rowArray = JsonArray()
        rowArray.add(liquidacion.fecha)
        rowArray.add(liquidacion.archivo)
        rowArray.add(liquidacion.concepto)
        rowArray.add(liquidacion.monto)
        rowArray.add(liquidacion.estado)
        jsonRequest.add("rows", rowArray)
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun updateLiquidacion(liquidacion: liquidacion): JsonElement? {

        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "updateLiquidacion")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "LIQUIDACIONES")
        val rowArray = JsonArray()
        rowArray.add(liquidacion.id)
        rowArray.add(liquidacion.fecha)
        rowArray.add(liquidacion.archivo)
        rowArray.add(liquidacion.concepto)
        rowArray.add(liquidacion.monto)
        rowArray.add("Reembolsado")
        jsonRequest.add("rows", rowArray)
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun deleteLiquidacion(liquidacion: liquidacion): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteLiquidacion")
        jsonRequest.addProperty("idSheet", idSheet)
        jsonRequest.addProperty("sheet", "LIQUIDACIONES")
        val rowArray = JsonArray()
        rowArray.add(liquidacion.id)
        rowArray.add(liquidacion.archivo)
        rowArray.add(liquidacion.concepto)
        jsonRequest.add("rows", rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getResumenGastos(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getResumengatos")
        jsonRequest.addProperty("idFile", idFile)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }
}