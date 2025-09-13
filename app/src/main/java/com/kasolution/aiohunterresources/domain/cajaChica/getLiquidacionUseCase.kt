package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class getLiquidacionUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId): Result<ArrayList<liquidacion>> {
        var lista = ArrayList<liquidacion>()

        val responseResult = repository.getLiquidacion(urlid)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val respuesta = response?.get("Respuesta")
                if (respuesta != null && respuesta.asString == "exito") {
                    val data = response.getAsJsonArray("Resultado")
                    if (data != null) {
                        for (i in data.size() - 1 downTo 0) {
                            val jsonObject = data.get(i).asJsonObject
                            val id = jsonObject.get("CODIGO").asString
                            val fecha = jsonObject.get("FECHA").asString
                            val archivo = jsonObject.get("ARCHIVO").asString
                            val concepto = jsonObject.get("CONCEPTO").asString
                            val monto = jsonObject.get("MONTO").asString
                            val estado = jsonObject.get("ESTADO").asString
                            lista.add(liquidacion(id, fecha, archivo, concepto, monto, estado,"",false))
                        }
                    }
                }
                Result.success(lista)
            }

            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }

        }
    }
}