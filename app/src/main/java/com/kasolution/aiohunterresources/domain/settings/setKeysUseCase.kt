package com.kasolution.aiohunterresources.domain.settings

import android.util.Log
import com.kasolution.aiohunterresources.UI.Settings.view.model.userKey
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class setKeysUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId,user:String, userKey: userKey): Result<String> {
        val responseResult = repository.setKeys(urlid, user,userKey)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val keys= response?.get("status")
                Result.success(keys.toString())
            }

            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}