package com.kasolution.aiohunterresources.domain.fichasTecnicas

import android.util.Log
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryFichasTecnicas


class getBrandUseCase() {
    private val repository= RepositoryFichasTecnicas()
    suspend operator fun invoke(urlId: urlId):Result<ArrayList<Brand>>{
        var lista=ArrayList<Brand>()

        val responseResult=repository.getBrand(urlId)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
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
                Result.success(lista)
            }
            false->{
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }

    }
}