package com.kasolution.aiohunterresources.domain.user

import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class getUserUseCase() {
    private val repository= RepositoryAccess()
    suspend operator fun invoke(urlid: urlId):ArrayList<user>{
        var lista=ArrayList<user>()

        val response=repository.getUser(urlid).asJsonObject
        val data = response?.getAsJsonArray("Resultado")
        if (data != null) {
            for (i in 0 until data.size()) {
                val jsonObject = data.get(i).asJsonObject
                val id = jsonObject.get("ID").asString
                val name = jsonObject.get("NAME").asString
                val lastName = jsonObject.get("LAST NAME").asString
                val user = jsonObject.get("USER").asString
                val password = jsonObject.get("PASSWORD").asString
                val tipo = jsonObject.get("TIPO").asString
                lista.add(user(id,name,lastName,user,password,tipo))
            }
        }
        return lista
    }
}