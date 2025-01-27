package com.kasolution.aiohunterresources.domain.fichasTecnicas

import android.util.Log
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryFichasTecnicas

class updateModelUseCase() {
    private val repository= RepositoryFichasTecnicas()
    suspend operator fun invoke(urlId: urlId,datos:List<String>): Result<VehicleModel> {
        lateinit var datamodel: VehicleModel
        val responseResult=repository.updateModel(urlId,datos)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Resultado")
                if (data!=null){
                    for (i in 0 until data.size()) {
                        if (data.get(i).isJsonObject) {
                            val jsonObject = data.get(i).asJsonObject
                            val id = jsonObject.get("ID").asString
                            val marca = jsonObject.get("MARCA").asString
                            val modelo = jsonObject.get("MODELO").asString
                            val imagen = jsonObject.get("IMAGEN").asString
                            val comentarios = jsonObject.get("COMENTARIOS").asString
                            val basica = jsonObject.get("BASICA").asString
                            val otros = jsonObject.get("EXTRA").asString
                            val autor = jsonObject.get("AUTOR").asString
                            val aprobado = jsonObject.get("APROBADO").asString
                            val estado = jsonObject.get("ESTADO").asString
                            datamodel = VehicleModel(id,marca,modelo,imagen,comentarios,basica,otros,autor,aprobado,estado)
                        }
                    }
                }else{
                    datamodel = VehicleModel("", "", "", "", "", "","","","","")
                }
                Result.success(datamodel)
            }
            false->{
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}