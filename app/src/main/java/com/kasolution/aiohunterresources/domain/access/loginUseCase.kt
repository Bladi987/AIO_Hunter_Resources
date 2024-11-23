package com.kasolution.aiohunterresources.domain.access

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess

class loginUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId, user:user): user {
        lateinit var datauser: user
        val response = repository.login(urlid,user).asJsonObject
        Log.i("BladiDevMonitor", response.toString())
        var objeto = response.get("Resultado")
        if (objeto.isJsonPrimitive) {
            if (objeto.asString == "not found")
                datauser = user("", "", "", "", "", "")
        } else if (objeto.isJsonArray) {
            val data = response?.getAsJsonArray("Resultado")
            if (data != null) {
                for (i in 0 until data.size()) {
                    if (data.get(i).isJsonObject) {
                        val jsonObject = data.get(i).asJsonObject
                        val id = jsonObject.get("ID").asString
                        val name = jsonObject.get("NAME").asString
                        val lastName = jsonObject.get("LAST NAME").asString
                        val user = jsonObject.get("USER").asString
                        val password = jsonObject.get("PASSWORD").asString
                        val tipo = jsonObject.get("TIPO").asString
                        datauser = user(id, name, lastName, user, password, tipo)
                    }
                }
            } else {
                datauser = user("", "", "", "", "", "")
            }
        }
        return datauser
    }
}