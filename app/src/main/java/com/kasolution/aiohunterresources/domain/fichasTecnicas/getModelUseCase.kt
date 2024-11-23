package com.kasolution.aiohunterresources.domain.fichasTecnicas


import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryFichasTecnicas

class getModelUseCase() {
    private val repository= RepositoryFichasTecnicas()
    suspend operator fun invoke(urlId: urlId,filter:String, column:Int,state:String):ArrayList<VehicleModel>{
        var lista=ArrayList<VehicleModel>()

        val response=repository.getModel(urlId,filter,column).asJsonObject
        val data = response?.getAsJsonArray("Resultado")
        if (data != null) {
            for (i in 0 until data.size()) {
                val jsonObject = data.get(i).asJsonObject
                val id = jsonObject.get("ID").asString
                val marca = jsonObject.get("MARCA").asString
                val modelo = jsonObject.get("MODELO").asString
                val imagen = jsonObject.get("IMAGEN").asString
                val comentarios = jsonObject.get("COMENTARIOS").asString
                val basica = jsonObject.get("BASICA").asString
                val extra = jsonObject.get("EXTRA").asString
                val autor = jsonObject.get("AUTOR").asString
                val aprobado = jsonObject.get("APROBADO").asString
                val estado = jsonObject.get("ESTADO").asString
                if (state=="Publicado"){
                    if (estado=="Publicado")
                        lista.add(VehicleModel(id,marca,modelo,imagen,comentarios,basica,extra,autor,aprobado,estado))
                }else{
                    if (estado!="Publicado")
                        lista.add(VehicleModel(id,marca,modelo,imagen,comentarios,basica,extra,autor,aprobado,estado))
                }

            }
        }
        return lista
    }
}