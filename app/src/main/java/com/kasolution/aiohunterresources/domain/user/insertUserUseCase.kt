package com.kasolution.aiohunterresources.domain.user

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class insertUserUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId, user: user): Result<user> {
        lateinit var datauser: user
        val responseResult = repository.insertUser(urlid, user)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Respuesta")
                if (data != null) {
                    val id = data[0].asString
                    val name = data[1].asString
                    val lastName = data[2].asString
                    val identification=data[3].asString
                    val user = data[4].asString
                    val password = data[5].asString
                    val tipo = data[6].asString
                    datauser = user(id, name, lastName, identification,user, password, tipo,"")
                } else {
                    datauser = user("", "", "", "", "", "","","")
                }
                Result.success(datauser)
            }

            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}