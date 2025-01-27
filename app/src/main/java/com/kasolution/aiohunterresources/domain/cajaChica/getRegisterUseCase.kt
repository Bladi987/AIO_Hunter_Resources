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
                        val id = jsonObject.get("id").asString
                        val fecha = jsonObject.get("Fecha").asString
                        val ciudad = jsonObject.get("Ciudad").asString
                        val tipoDoc = jsonObject.get("TipoDoc").asString
                        val nroDoc = jsonObject.get("NroDoc").asString
                        val proveedor = jsonObject.get("Proveedor").asString
                        val descripcion = jsonObject.get("Descripcion").asString
                        var c_movilidad = ""
                        var c_alimentacion = ""
                        var c_alojamiento = ""
                        var c_otros = ""
                        var s_movilidad = ""
                        var s_alimentacion = ""
                        var s_alojamiento = ""
                        var s_otros = ""
                        var Monto = ""

                        when (jsonObject.size()) {
                            8 -> c_movilidad =
                                formatearMonto(jsonObject.get("cs-Movilidad").asString)

                            9 -> c_alimentacion =
                                formatearMonto(jsonObject.get("cs-Alimentación").asString)

                            10 -> c_alojamiento =
                                formatearMonto(jsonObject.get("cs-Alojamiento").asString)

                            11 -> c_otros = formatearMonto(jsonObject.get("cs-Otros").asString)
                            12 -> s_movilidad =
                                formatearMonto(jsonObject.get("ss-Movilidad").asString)

                            13 -> s_alimentacion =
                                formatearMonto(jsonObject.get("ss-Alimentación").asString)

                            14 -> s_alojamiento =
                                formatearMonto(jsonObject.get("ss-Alojamiento").asString)

                            15 -> s_otros = formatearMonto(jsonObject.get("ss-Otros").asString)
                        }

                        lista.add(
                            register(
                                id,
                                fecha,
                                ciudad,
                                tipoDoc,
                                nroDoc,
                                proveedor,
                                descripcion,
                                c_movilidad,
                                c_alimentacion,
                                c_alojamiento,
                                c_otros,
                                s_movilidad,
                                s_alimentacion,
                                s_alojamiento,
                                s_otros
                            )
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
