package com.kasolution.aiohunterresources.domain.cajaChica


import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica
import org.json.JSONArray


class getFileDetailsUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId): ArrayList<fileDetails> {
        var lista = ArrayList<fileDetails>()

        val response = repository.getDetailsFile(urlId).asJsonObject
        val data = response?.getAsJsonArray("Resultado")

        if (data != null) {
            for (i in data.size() - 1 downTo 0) {
                val nombreReal = data[i].asString
                val nombre = generarNombres(nombreReal)
                lista.add(fileDetails(nombreReal,nombre))
            }
        }
        return lista
    }

    private fun generarNombres(nombre: String?) :String{
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