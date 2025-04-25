package com.kasolution.aiohunterresources.domain.access

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess

class loginUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId, user:user): Result<user> {
        lateinit var datauser: user
        val responseResult = repository.login(urlid,user)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                var objeto = response?.get("Resultado")
                if (objeto!!.isJsonPrimitive) {
                    if (objeto.asString == "not found")
                        datauser = user("", "", "", "", "", "","")
                } else if (objeto.isJsonArray) {
                    val data = response?.getAsJsonArray("Resultado")
                    Log.i("BladiDev", "Data: $data")
                    if (data != null) {
                        for (i in 0 until data.size()) {
                            if (data.get(i).isJsonObject) {
                                val jsonObject = data.get(i).asJsonObject
                                val id = jsonObject.get("ID").asString
                                val name = jsonObject.get("NAME").asString
                                val lastName = jsonObject.get("LAST NAME").asString
                                val user = jsonObject.get("USER").asString
                                val password = jsonObject.get("PASSWORD").asString
                                val tipo = jsonObject.get("TIPO").asString
                                val keys = jsonObject.get("keys")
                                Log.i("BladiDev", "Key: $keys")
                                datauser = user(id, name, lastName, user, password, tipo,keys.toString())
                            }
                        }
                    } else {
                        datauser = user("", "", "", "", "", "","")
                    }
                }
                Result.success(datauser)
            }
            false -> {
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}