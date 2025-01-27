package com.kasolution.aiohunterresources.domain.cajaChica


import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica



class getFileDetailsUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId): Result<ArrayList<fileDetails>> {
        val lista = ArrayList<fileDetails>()
        val responseResult = repository.getDetailsFile(urlId)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Resultado")
                if (data != null) {
                    for (i in data.size() - 1 downTo 0) {
                        val nombreReal = data[i].asString
                        val nombre = generarNombres(nombreReal)
                        lista.add(fileDetails(nombreReal, nombre))
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

    private fun generarNombres(nombre: String?): String {
        // Buscar la posición del delimitador '->'
        val posicionDelimitador = nombre!!.indexOf("->")

        // Si el delimitador está presente, cortar el texto antes de él
        return if (posicionDelimitador != -1) {
            nombre.substring(0, posicionDelimitador).trim()
        } else {
            // Si no está presente, devolver el texto original
            nombre.trim()
        }
    }
}