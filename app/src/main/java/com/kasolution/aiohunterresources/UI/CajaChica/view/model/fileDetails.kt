package com.kasolution.aiohunterresources.UI.CajaChica.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class fileDetails(
    @SerializedName("NOMBREREAL") val nombreReal: String,
    @SerializedName("NOMBRE") val nombre: String
): Serializable