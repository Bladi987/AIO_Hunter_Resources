package com.kasolution.aiohunterresources.domain.user

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class deleteUserUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId, user: user): Result<String> {
        lateinit var responseId: String
        val responseResult = repository.deleteUser(urlid, user)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Resultado")
                if (response != null) responseId = "done"
                else responseId = "error"
                Result.success(responseId)
            }
            false->{
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }




    }

}