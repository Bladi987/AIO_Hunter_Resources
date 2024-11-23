package com.kasolution.aiohunterresources.UI.CajaChica.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class liquidacion(
    @SerializedName("ID") val id: String,
    @SerializedName("FECHA") val fecha: String,
    @SerializedName("ARCHIVO") val archivo: String,
    @SerializedName("CONCEPTO") val concepto: String,
    @SerializedName("MONTO") val monto: String,
    @SerializedName("ESTADO") val estado: String
): Serializable