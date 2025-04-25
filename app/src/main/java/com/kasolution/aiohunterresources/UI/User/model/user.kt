package com.kasolution.aiohunterresources.UI.User.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class user(
    @SerializedName("ID")val id: String,
    @SerializedName("NAME")val name: String,
    @SerializedName("LASTNAME")val lastName: String,
    @SerializedName("USER")val user: String,
    @SerializedName("PASSWORD") var password: String,
    @SerializedName("TIPO")val tipo: String,
    @SerializedName("KEYS")var keys: String
):Serializable