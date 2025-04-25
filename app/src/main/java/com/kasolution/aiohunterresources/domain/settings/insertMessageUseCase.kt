package com.kasolution.aiohunterresources.domain.settings

import android.util.Log
import com.kasolution.aiohunterresources.UI.Settings.view.model.itemContactUs
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class insertMessageUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId, itemContactUs: itemContactUs): Result<itemContactUs> {
        val responseResult = repository.insertMessage(urlid, itemContactUs)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Respuesta")
                val usuario = data!![0].asString
                val email = data[1].asString
                val fecha = data[2].asString
                val mensaje = data[3].asString
                Result.success(itemContactUs(usuario, email, fecha, mensaje))
            }
            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}