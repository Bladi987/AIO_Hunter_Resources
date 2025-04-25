package com.kasolution.aiohunterresources.domain.user

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class getUserUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId): Result<ArrayList<user>> {
        var lista = ArrayList<user>()
        val responseResult = repository.getUser(urlid)
        Log.i("BladiDev", "responseResult: $responseResult")
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Resultado")
                if (data != null) {
                    for (i in 0 until data.size()) {
                        val jsonObject = data.get(i).asJsonObject
                        val id = jsonObject.get("ID").asString
                        val name = jsonObject.get("NAME").asString
                        val lastName = jsonObject.get("LAST NAME").asString
                        val user = jsonObject.get("USER").asString
                        val password = jsonObject.get("PASSWORD").asString
                        val tipo = jsonObject.get("TIPO").asString
                        lista.add(user(id, name, lastName, user, password, tipo,""))
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