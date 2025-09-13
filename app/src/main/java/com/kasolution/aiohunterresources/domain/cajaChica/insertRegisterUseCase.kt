package com.kasolution.aiohunterresources.domain.cajaChica


import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class insertRegisterUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId, register: register): Result<register> {
        lateinit var registro: register
        val responseResult = repository.insertRegister(urlId, register)
        Log.i("BladiDev", "respuesta: $responseResult")
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.asJsonObject?.getAsJsonArray("Respuesta")

                if (data != null) {
                    val id = data[0].asString
                    val fecha = data[1].asString
                    val tipoDoc = data[2].asString
                    val nroDoc = data[3].asString
                    val ruc = data[4].asString
                    val proveedor = data[5].asString
                    val detalle = data[6].asString
                    val motivo = data[7].asString
                    val tipoGasto = data[8].asString
                    val monto = data[9].asString

                    registro = register(id, fecha, tipoDoc, nroDoc, ruc, proveedor, detalle, motivo, tipoGasto, formatearMonto(monto))

                } else registro =
                    register("", "", "", "", "", "", "", "", "", "")

                Result.success(registro)
            }

            false -> {
                // Si la llamada al repositorio falló, capturamos el mensaje de error
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
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