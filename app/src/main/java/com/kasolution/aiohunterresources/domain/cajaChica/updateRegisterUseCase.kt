package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica

class updateRegisterUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId, register: register): register {
        lateinit var registro: register
        val response = repository.updateRegister(urlId, register).asJsonObject
        Log.i("BladiDev",response.toString())
        val data = response?.getAsJsonArray("Respuesta")
        if (data != null) {
            val id = data[0].asString
            val fecha = data[1].asString
            val ciudad = data[2].asString
            val tipoDoc = data[3].asString
            val nroDoc = data[4].asString
            val proveedor = data[5].asString
            val descripcion = data[6].asString
            val c_movilidad = formatearMonto(data[7].asString)
            val c_alimentacion = formatearMonto(data[8].asString)
            val c_alojamiento = formatearMonto(data[9].asString)
            val c_otros = formatearMonto(data[10].asString)
            val s_movilidad = formatearMonto(data[11].asString)
            val s_alimentacion = formatearMonto(data[12].asString)
            val s_alojamiento = formatearMonto(data[13].asString)
            val s_otros = formatearMonto(data[14].asString)


            registro=register(
                id,
                fecha,
                ciudad,
                tipoDoc,
                nroDoc,
                proveedor,
                descripcion,
                c_movilidad,
                c_alimentacion,
                c_alojamiento,
                c_otros,
                s_movilidad,
                s_alimentacion,
                s_alojamiento,
                s_otros
            )
        } else registro = register("", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        return registro
    }

    private fun formatearMonto(monto: String): String {
        var Monto = ""
        if (monto != "") {
            Monto = monto.replace("S/.", "S/ ")
            Monto = Monto.replace(",", ".")
        }
        return Monto
    }
}