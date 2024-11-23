package com.kasolution.aiohunterresources.domain.user

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class updateUserUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId, user: user): user {
        lateinit var datauser: user
        val response = repository.updateUser(urlid, user).asJsonObject
        Log.i("BladiDevMonitor", response.toString())
        val data = response?.getAsJsonArray("Respuesta")
        if (data != null) {
            datauser = user(
                data[0].asString,
                data[1].asString,
                data[2].asString,
                data[3].asString,
                data[4].asString,
                data[5].asString
            )
        } else {
            datauser = user("", "", "", "", "", "")
        }
        return datauser
    }

}