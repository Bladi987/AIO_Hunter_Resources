package com.kasolution.aiohunterresources.UI.CajaChica.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class register(
    @SerializedName("ID") val id: String,
    @SerializedName("FECHA") val fecha: String,
    @SerializedName("TIPO_DOC") val tipoDoc: String,
    @SerializedName("NRO_DOC") val nroDoc: String,
    @SerializedName("RUC") val ruc: String,
    @SerializedName("PROVEEDOR") val proveedor: String,
    @SerializedName("DETALLE") val detalle: String,
    @SerializedName("MOTIVO") val motivo: String,
    @SerializedName("TIPO_GASTO") val tipoGasto: String,
    @SerializedName("MONTO") val monto: String
): Serializable