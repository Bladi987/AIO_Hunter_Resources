package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class VehicleModel(
    @SerializedName("ID") val id: String,
    @SerializedName("MARCA") val marca: String,
    @SerializedName("MODELO") val modelo: String,
    @SerializedName("IMAGEN") val imagen: String,
    @SerializedName("COMENTARIOS") val comentarios: String,
    @SerializedName("BASICA") val basica: String,
    @SerializedName("EXTRA") val extra: String,
    @SerializedName("AUTOR") val autor:String,
    @SerializedName("APROBADO") val aprobado:String,
    @SerializedName("ESTADO") val estado:String
): Serializable
