package com.kasolution.aiohunterresources.domain.user

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class updateUserUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId, user: user): Result<user> {
        lateinit var datauser: user
        val responseResult = repository.updateUser(urlid, user)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
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