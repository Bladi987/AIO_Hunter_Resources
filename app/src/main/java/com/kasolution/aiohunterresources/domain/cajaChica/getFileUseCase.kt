package com.kasolution.aiohunterresources.domain.cajaChica


import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class getFileUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId): ArrayList<file> {
        var lista = ArrayList<file>()
        val response = repository.getFile(urlId).asJsonObject
        val data = response?.getAsJsonArray("Resultado")
        if (data != null) {
            for (i in 0 until data.size()) {
                val jsonObject = data.get(i).asJsonObject
                val id = jsonObject.get("id").asString
                val name = jsonObject.get("name").asString
                if (!name.contains("liquidaciones",true)) {
                    lista.add(file(id, name))
                }
            }
        }
        return lista
    }
}