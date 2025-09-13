package com.kasolution.aiohunterresources.domain.splashScreen


import android.util.Log
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess

class getSettingsUseCase() {
    private val repository = RepositoryAccess()
    private val log = "BladiDevGetSettingsUseCase"

    suspend operator fun invoke(urlId: urlId): Result<Pair<String?, String?>> {
        val versionAPP : String?
        val linkUpdate: String?
        val responseResult = repository.getSettings(urlId)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull() // No necesitamos el safe call aquí
                val jsonResponse = response?.asJsonObject
                Log.i(log, "respuesta: $jsonResponse")
                val data = jsonResponse?.getAsJsonObject("respuesta")
                versionAPP = data?.get("VERSION")?.asString
                linkUpdate = data?.get("LINK-UPDATE")?.asString

                Log.i(log, "link: $linkUpdate")
                Result.success(Pair(versionAPP, linkUpdate))
            }

            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}