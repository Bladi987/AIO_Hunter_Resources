package com.kasolution.aiohunterresources.domain.splashScreen


import android.util.Log
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess

class getSettingsUseCase() {
    private val repository = RepositoryAccess()
    private val log = "BladiDevGetSettingsUseCase"

    suspend operator fun invoke(urlId: urlId): Result<String> {
        var versionAPP = ""  // Inicializamos el String de respuesta
        val responseResult = repository.getSettings(urlId)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                Log.i(log, "respuesta: $response")
                val data = response?.getAsJsonObject("respuesta")
                versionAPP = data?.get("VERSION")!!.asString

//                for (i in 0 until data.size()) {
//                    val jsonObject = data.get(i).asJsonObject
//                    versionAPP = jsonObject.get("VERSION").asString
//                }

                Result.success(versionAPP)
            }

            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}