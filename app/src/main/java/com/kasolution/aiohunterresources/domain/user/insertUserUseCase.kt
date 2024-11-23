package com.kasolution.aiohunterresources.domain.user

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class insertUserUseCase() {
    private val repository= RepositoryAccess()
    suspend operator fun invoke(urlid: urlId,user:user): user {
        lateinit var datauser: user
        val response=repository.insertUser(urlid,user).asJsonObject
        Log.i("BladiDevUser",response.toString())
        val data = response?.getAsJsonArray("Respuesta")
        if (data!=null){
            val id = data[0].asString
            val name = data[1].asString
            val lastName = data[2].asString
            val user = data[3].asString
            val password = data[4].asString
            val tipo = data[5].asString
            datauser = user(id, name, lastName, user, password, tipo)
        }else{
            datauser = user("", "", "", "", "", "")
        }
        return datauser
    }
}