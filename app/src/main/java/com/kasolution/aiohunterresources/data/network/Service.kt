package com.kasolution.recursoshunter.data.network

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Service(idScript: String, idSheet: String, sheetname: String? = null) {
    val log = "BladiDevService"

    private val urlScript =
        "https://script.google.com/macros/s/$idScript/" //este sera la url para la script
    private val urlSheet =
        "https://docs.google.com/spreadsheets/d/$idSheet/" // remplazar por id del documentos sheet(aqui puede ir el id del documento sheet o el id de la carpeta)
    private val idFile =
        idSheet    //variable temporal mientras se migra a utilizar solo id y no url
    private val sheetName = sheetname

    //privateModule
    private val retrofit = RetrofitHelper.getRetrofit(urlScript)


    //brandModule
    suspend fun getBrand(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getData")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "Brand")


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
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
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
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "VEHICLEDATA")

        jsonRequest.addProperty("data", data[0])
        jsonRequest.addProperty("type", data[1])
        jsonRequest.addProperty("marca", data[2])
        jsonRequest.addProperty("modelo", data[3])
        jsonRequest.addProperty("comentarios", data[4])
        jsonRequest.addProperty("basico", data[5])
        jsonRequest.addProperty("otros", data[6])
        jsonRequest.addProperty("tecnico", data[7])
        jsonRequest.addProperty("aprobado", data[8])
        jsonRequest.addProperty("estado", data[9])

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun updateModel(data: List<String>): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "editModel")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "VEHICLEDATA")
        jsonRequest.addProperty("id", data[0])
        jsonRequest.addProperty("data", data[1])
        jsonRequest.addProperty("type", data[2])
        jsonRequest.addProperty("marca", data[3])
        jsonRequest.addProperty("modelo", data[4])
        jsonRequest.addProperty("linkimg", data[5])
        jsonRequest.addProperty("comentarios", data[6])
        jsonRequest.addProperty("basica", data[7])
        jsonRequest.addProperty("otros", data[8])
        jsonRequest.addProperty("tecnico", data[9])
        jsonRequest.addProperty("aprobado", data[10])
        jsonRequest.addProperty("estado", data[11])
        Log.i("BladiDevMonitorio", jsonRequest.toString())
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun deleteModel(id: String): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteModel")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "VEHICLEDATA")
        jsonRequest.addProperty("id", id)
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }


    //secureModule
    suspend fun login(user: String, password: String): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "login")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "User")

        jsonRequest.addProperty("user", user)
        jsonRequest.addProperty("password", password)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getUser(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "readUsers")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "User")


        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun insertUser(data: List<String>): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "addUser")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "User")

        jsonRequest.addProperty("name", data[0])
        jsonRequest.addProperty("lastname", data[1])
        jsonRequest.addProperty("user", data[2])
        jsonRequest.addProperty("password", data[3])
        jsonRequest.addProperty("tipo", data[4])

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun updateUser(data: List<String>): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "editUser")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "User")
        jsonRequest.addProperty("id", data[0])
        jsonRequest.addProperty("name", data[1])
        jsonRequest.addProperty("lastname", data[2])
        jsonRequest.addProperty("user", data[3])
        jsonRequest.addProperty("password", data[4])
        jsonRequest.addProperty("tipo", data[5])

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun deleteUser(id: String): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteUser")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
        jsonRequest.addProperty("sheet", "User")
        jsonRequest.addProperty("id", id)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }
    //settingsModule

    suspend fun getSettings(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getSettings")
        jsonRequest.addProperty(
            "urlSheet", urlSheet
        )
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

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getDetailsFile(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getSheet")
        jsonRequest.addProperty("idSheet", idFile)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun getRegister(): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "getData")
        jsonRequest.addProperty("idSheet", idFile)
        jsonRequest.addProperty("sheet", sheetName)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }

    suspend fun insertRegister(registro:register): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "addRegister")
        jsonRequest.addProperty("idSheet", idFile)
        jsonRequest.addProperty("sheet", sheetName)

        val rowArray = JsonArray()
        val row1=JsonArray()
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
        jsonRequest.add("rows",rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }
    suspend fun updateRegister(registro:register): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "updateRegister")
        jsonRequest.addProperty("idSheet", idFile)
        jsonRequest.addProperty("sheet", sheetName)

        val rowArray = JsonArray()
        val row1=JsonArray()
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
        jsonRequest.add("rows",rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }
    suspend fun deleteRegister(registro:register): JsonElement? {
        val jsonRequest = JsonObject()
        jsonRequest.addProperty("action", "deleteRegister")
        jsonRequest.addProperty("idSheet", idFile)
        jsonRequest.addProperty("sheet", sheetName)

        val rowArray = JsonArray()
        val row1=JsonArray()
        row1.add(registro.id)         //se envia el id el cual sera el index dentro del sheet
        row1.add(registro.fecha)     //fecha
        row1.add(registro.ciudad)    //ciudad
        row1.add(registro.tipoDoc) //tipo de documento
        row1.add(registro.nroDoc) //nro documento
        row1.add(registro.proveedor) //proveedor
        row1.add(registro.descripcion) //descripcion

        rowArray.add(row1)
        jsonRequest.add("rows",rowArray)

        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).peticion(jsonRequest)
            response.body()
        }
    }
}