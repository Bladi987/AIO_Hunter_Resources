package com.kasolution.aiohunterresources.data

import com.google.gson.JsonElement
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.recursoshunter.data.network.Service

class RepositoryAccess() {
    val log="BladiDevRepositoryAccess"
    private var api:Service?=null
    private fun procesarUrl(urlId: urlId){
        api= Service(urlId)
    }

    //secureModule
    suspend fun login(urlid: urlId, user: user): JsonElement {
        procesarUrl(urlid)
        val response = api!!.login(user)
        return response!!
    }

    suspend fun getUser(urlid: urlId): JsonElement {
        procesarUrl(urlid)
        val response = api!!.getUser()
        return response!!
    }
    suspend fun insertUser(urlid: urlId, user: user): JsonElement {
        procesarUrl(urlid)
        val response = api!!.insertUser(user)
        return response!!
    }
    suspend fun updateUser(urlid: urlId, user: user): JsonElement {
        procesarUrl(urlid)
        val response = api!!.updateUser(user)
        return response!!
    }
    suspend fun deleteUser(urlid: urlId, user: user): JsonElement {
        procesarUrl(urlid)
        val response = api!!.deleteUser(user)
        return response!!
    }
    //settingModule
    suspend fun getSettings(urlid: urlId): JsonElement {
        procesarUrl(urlid)
        val response = api!!.getSettings()
        return response!!
    }
}