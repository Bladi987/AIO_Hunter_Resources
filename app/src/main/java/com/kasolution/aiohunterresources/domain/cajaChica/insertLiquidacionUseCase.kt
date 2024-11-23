package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class insertLiquidacionUseCase() {
    private val repository= RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId,liquidacion:liquidacion): liquidacion {
        lateinit var dataliquidacion: liquidacion
        val response=repository.insertLiquidacion(urlid,liquidacion).asJsonObject
        Log.i("BladiDevUser",response.toString())
        val data = response?.getAsJsonArray("Respuesta")
        if (data!=null){
            val id = data[0].asString
            val fecha = data[1].asString
            val archivo = data[2].asString
            val concepto = data[3].asString
            val monto = data[4].asString
            val estado = data[5].asString
            dataliquidacion = liquidacion(id, fecha, archivo, concepto, monto, estado)
        }else{
            dataliquidacion = liquidacion("", "", "", "", "", "")
        }
        return dataliquidacion
    }
}