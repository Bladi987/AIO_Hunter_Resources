package com.kasolution.aiohunterresources.domain.cajaChica


import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class getResumenGastosUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId):  String {
        lateinit var saldoContable: String
        val response = repository.getResumenGastos(urlId).asJsonObject
        val data=response?.get("Resultado")
        if (data != null) return data.asString
        else return ""
    }
}