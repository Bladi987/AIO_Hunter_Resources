package com.kasolution.aiohunterresources.domain.fichasTecnicas

import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryFichasTecnicas


class deleteModelUseCase() {
    private val repository= RepositoryFichasTecnicas()
    suspend operator fun invoke(urlId: urlId, id:String):String{
        lateinit var responseId: String
        val response=repository.deleteModel(urlId,id).asJsonObject
        var objeto = response.get("id")
            responseId=objeto.asString
        return responseId
    }

}