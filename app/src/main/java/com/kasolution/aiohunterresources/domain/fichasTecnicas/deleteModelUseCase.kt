package com.kasolution.aiohunterresources.domain.fichasTecnicas

import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryFichasTecnicas


class deleteModelUseCase() {
    private val repository= RepositoryFichasTecnicas()
    suspend operator fun invoke(urlId: urlId, id:String):Result<String>{
        lateinit var responseId: String
        val responseResult=repository.deleteModel(urlId,id)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                var objeto = response?.get("id")
                responseId=objeto!!.asString
                Result.success(responseId)
            }
            false->{
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Result.failure(Exception(errorMessage))
            }
        }
    }

}