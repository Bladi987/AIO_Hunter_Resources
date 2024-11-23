package com.kasolution.aiohunterresources.core.dataConexion

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//data class urlId(
//    @SerializedName("idScript") val idScript: String,   //idScriptPrincipal de la url
//    @SerializedName("idFile") val idFile: String,
//    @SerializedName("idSheet") val idSheet: String,
//    @SerializedName("sheetName") val sheetName: String
//): Serializable
data class urlId(
    val idScript: String,
    val idFile: String,
    val idSheet: String,
    val sheetName: String
)