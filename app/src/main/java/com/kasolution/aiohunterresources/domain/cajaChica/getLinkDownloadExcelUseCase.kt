package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class getLinkDownloadExcelUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId): Result<String> {
        lateinit var downloadLlink: String
        val responseResult = repository.getDownloadLink(urlid)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.get("link")
                data?.let {link->
                    downloadLlink = link.asString
                }
                Result.success(downloadLlink)
            }
            false -> {
                // Si la llamada al repositorio falló, capturamos el mensaje de error
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}



