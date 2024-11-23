package com.kasolution.aiohunterresources.domain.fichasTecnicas

import android.util.Log
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryFichasTecnicas


class getBrandUseCase() {
    private val repository= RepositoryFichasTecnicas()
    suspend operator fun invoke(urlId: urlId):ArrayList<Brand>{
        var lista=ArrayList<Brand>()

        val response=repository.getBrand(urlId).asJsonObject
        val data = response?.getAsJsonArray("Resultado")
        if (data != null) {
            for (i in 0 until data.size()) {
                val jsonObject = data.get(i).asJsonObject
                val id = jsonObject.get("ID").asString
                val marca = jsonObject.get("BRAND").asString
                val logo = jsonObject.get("LOGO").asString
                val estado = jsonObject.get("ESTADO").asString
                if (estado=="TRUE") {
                    lista.add(Brand(id, marca, logo, estado))
                }
            }
        }
        return lista
    }
}