package com.kasolution.aiohunterresources.domain.controlEquipos

import android.util.Log
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.model.equipos
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryControlEquipos


class updateEquipoUseCase() {
    private val repository = RepositoryControlEquipos()
    suspend operator fun invoke(urlId: urlId, equipo: equipos): Result<equipos> {

        val responseResult = repository.updateEquipo(urlId, equipo)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Resultado")
                if (data != null && data.size() >= 7) {
                    val dataEquipo = equipos(
                        vid = data[0].asString,
                        marca = data[1].asString,
                        modelo = data[2].asString,
                        tecnico = data[3].asString,
                        fechaEntrega = data[4].asString,
                        estado = data[5].asString,
                        comentarios = data[6].asString
                    )
                    Result.success(dataEquipo)
                } else {
                    Result.success(equipos("", "", "", "", "", "", ""))
                }
            }
            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}