package com.kasolution.aiohunterresources.UI.CajaChica.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class register(
    @SerializedName("ID") val id: String,
    @SerializedName("FECHA") val fecha: String,
    @SerializedName("CIUDAD") val ciudad: String,
    @SerializedName("TIPO_DOC") val tipoDoc: String,
    @SerializedName("NRO_DOC") val nroDoc: String,
    @SerializedName("PROVEEDOR") val proveedor: String,
    @SerializedName("DESCRIPCION") val descripcion: String,
    @SerializedName("C_MOVILIDAD") val c_movilidad: String,
    @SerializedName("C_ALIMENTACION") val c_alimentacion: String,
    @SerializedName("C_ALOJAMIENTO") val c_alojamiento: String,
    @SerializedName("C_OTROS") val c_otros: String,
    @SerializedName("S_MOVILIDAD") val s_movilidad: String,
    @SerializedName("S_ALIMENTACION") val s_alimentacion: String,
    @SerializedName("S_ALOJAMIENTO") val s_alojamiento: String,
    @SerializedName("S_OTROS") val s_otros: String
): Serializable