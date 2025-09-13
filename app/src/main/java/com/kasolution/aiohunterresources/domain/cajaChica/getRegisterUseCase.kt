package com.kasolution.aiohunterresources.domain.cajaChica


import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class getRegisterUseCase() {
    private val repository = RepositoryCajaChica()
    private val log = "BladiDevGetRegisterUseCase"

    suspend operator fun invoke(urlId: urlId): Result<ArrayList<register>> {
        val lista = ArrayList<register>()

        // Llamamos al repositorio para obtener el resultado de getRegister
        val responseResult = repository.getRegister(urlId)
        return when (responseResult.isSuccess) {
            true -> {
                // Si la llamada fue exitosa, procesamos la respuesta
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Resultado")

                if (data != null) {
                    for (i in data.size() - 1 downTo 0) {
                        val jsonObject = data.get(i).asJsonObject
                        val id = jsonObject.get("index").asString
                        val fecha = jsonObject.get("Fecha").asString
                        val tipoDoc = jsonObject.get("TipoDoc").asString
                        val nroDoc = jsonObject.get("NroDoc").asString
                        val ruc = jsonObject.get("Ruc").asString
                        val proveedor = jsonObject.get("Proveedor").asString
                        var detalle = jsonObject.get("Detalle").asString
                        var motivo = jsonObject.get("Motivo").asString
                        var tipoGasto = jsonObject.get("TipoGasto").asString
                        var monto = formatearMonto(jsonObject.get("Monto").asString)
                        lista.add(
                            register(id, fecha, tipoDoc, nroDoc, ruc, proveedor, detalle, motivo, tipoGasto, monto)
                        )
                    }
                }
                // Si la respuesta fue exitosa pero la data está vacía
                Result.success(lista)
            }

            false -> {
                // Si la llamada al repositorio falló, capturamos el mensaje de error
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e(log, "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }

    private fun formatearMonto(monto: String): String {
        var Monto = ""
        if (monto != "") {
            Monto = monto.replace("S/.", "S/ ")
            Monto = Monto.replace(",", ".")
        }
        return Monto
    }
}
