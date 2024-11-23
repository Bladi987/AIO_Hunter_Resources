package com.kasolution.aiohunterresources.domain.splashScreen


import android.util.Log
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess

class getSettingsUseCase() {
    private val repository= RepositoryAccess()
    private val log="BladiDevGetSettingsUseCase"
    suspend operator fun invoke(urlId: urlId): String {
        var versionAPP=""
        val response=repository.getSettings(urlId).asJsonObject
        val data = response?.getAsJsonArray("Respuesta")
        Log.i("respuesta",response.toString())
        if (data != null) {
            for (i in 0 until data.size()) {
                val jsonObject = data.get(i).asJsonObject
                val idscrippublic = jsonObject.get("IDSCRIPTPUBLIC").asString
                val idsheetpublic = jsonObject.get("IDSHEETPUBLIC").asString
                val idscriptprivate = jsonObject.get("IDSCRIPTPRIVATE").asString
                val idsheetprivate = jsonObject.get("IDSHEETPRIVATE").asString
                val version = jsonObject.get("VERSION").asString
                versionAPP=version
            }
        }
        return versionAPP
    }
}