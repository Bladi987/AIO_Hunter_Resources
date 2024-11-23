package com.kasolution.aiohunterresources.domain.cajaChica


import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class getRegisterUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(urlId: urlId): ArrayList<register> {
        var lista = ArrayList<register>()

        val response = repository.getRegister(urlId).asJsonObject
        val data = response?.getAsJsonArray("Resultado")
        if (data != null) {
            for (i in data.size() - 1 downTo 0) {
                val jsonObject = data.get(i).asJsonObject
                val id = jsonObject.get("id").asString
                val fecha = jsonObject.get("Fecha").asString
                val ciudad = jsonObject.get("Ciudad").asString
                val tipoDoc = jsonObject.get("TipoDoc").asString
                val nroDoc = jsonObject.get("NroDoc").asString
                val proveedor = jsonObject.get("Proveedor").asString
                val descripcion = jsonObject.get("Descripcion").asString
                var c_movilidad = ""
                var c_alimentacion = ""
                var c_alojamiento = ""
                var c_otros = ""
                var s_movilidad = ""
                var s_alimentacion = ""
                var s_alojamiento = ""
                var s_otros = ""
                var Monto = ""

                when (jsonObject.size()) {
                    8 -> {
                        c_movilidad = formatearMonto(jsonObject.get("cs-Movilidad").asString)
                        Monto = c_movilidad
                    }

                    9 -> {
                        c_alimentacion = formatearMonto(jsonObject.get("cs-Alimentación").asString)
                        Monto = c_alimentacion
                    }

                    10 -> {
                        c_alojamiento = formatearMonto(jsonObject.get("cs-Alojamiento").asString)
                        Monto = c_alojamiento
                    }

                    11 -> {
                        c_otros = formatearMonto(jsonObject.get("cs-Otros").asString)
                        Monto = c_otros
                    }

                    12 -> {
                        s_movilidad = formatearMonto(jsonObject.get("ss-Movilidad").asString)
                        Monto = s_movilidad
                    }

                    13 -> {
                        s_alimentacion = formatearMonto(jsonObject.get("ss-Alimentación").asString)
                        Monto = s_alimentacion
                    }

                    14 -> {
                        s_alojamiento = formatearMonto(jsonObject.get("ss-Alojamiento").asString)
                        Monto = s_alojamiento
                    }

                    15 -> {
                        s_otros = formatearMonto(jsonObject.get("ss-Otros").asString)
                        Monto = s_otros
                    }
                }

                lista.add(register(                    id,
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
                    s_otros))
            }
        }
        return lista
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