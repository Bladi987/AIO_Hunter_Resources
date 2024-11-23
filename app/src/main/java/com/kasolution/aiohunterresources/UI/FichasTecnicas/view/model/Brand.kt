package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Brand(
    @SerializedName("ID") val id: String,
    @SerializedName("MARCA") val brand: String,
    @SerializedName("LOGO") val icon: String,
    @SerializedName("ESTADO") val state: String
) : Serializable
