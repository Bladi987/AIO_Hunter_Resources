package com.kasolution.aiohunterresources.domain.controlEquipos

import android.util.Log
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.model.equipos
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryControlEquipos


class getEquiposUseCase() {
    private val repository = RepositoryControlEquipos()
    suspend operator fun invoke(urlId: urlId, user: String): Result<ArrayList<equipos>> {
        var lista = ArrayList<equipos>()

        val responseResult = repository.getEquipos(urlId, user)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Resultado")
                if (data != null) {
                    for (i in 0 until data.size()) {
                        val jsonObject = data.get(i).asJsonObject
                        val vid = jsonObject.get("VID").asString
                        val marca = jsonObject.get("MARCA").asString
                        val modelo = jsonObject.get("MODELO").asString
                        val tecnico = jsonObject.get("TECNICO").asString
                        val fEntrega = jsonObject.get("FECHA ENTREGA").asString
                        val estado = jsonObject.get("ESTADO").asString
                        val comentarios = jsonObject.get("COMENTARIOS")?.asString ?: ""

                        if (estado != "Instalado") {
                            lista.add(
                                equipos(
                                    vid,
                                    marca,
                                    modelo,
                                    tecnico,
                                    fEntrega,
                                    estado,
                                    comentarios
                                )
                            )
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