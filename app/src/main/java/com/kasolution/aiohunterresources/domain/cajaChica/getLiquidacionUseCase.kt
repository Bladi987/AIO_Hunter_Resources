package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class getLiquidacionUseCase() {
    private val repository= RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId):ArrayList<liquidacion>{
        var lista=ArrayList<liquidacion>()

        val response=repository.getLiquidacion(urlid).asJsonObject
        Log.i("BladiDev",response.toString())
        val data = response?.getAsJsonArray("Resultado")
        if (data != null) {
            for (i in 0 until data.size()) {
                val jsonObject = data.get(i).asJsonObject
                val id = jsonObject.get("CODIGO").asString
                val fecha = jsonObject.get("FECHA").asString
                val archivo = jsonObject.get("ARCHIVO").asString
                val concepto = jsonObject.get("CONCEPTO").asString
                val monto = jsonObject.get("MONTO").asString
                val estado = jsonObject.get("ESTADO").asString
                lista.add(liquidacion(id,fecha,archivo,concepto,monto,estado))
            }
        }
        return lista
    }
}