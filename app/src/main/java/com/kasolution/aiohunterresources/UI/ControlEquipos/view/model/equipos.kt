package com.kasolution.aiohunterresources.UI.ControlEquipos.view.model

data class equipos(
    val vid: String,
    val marca: String,
    val modelo: String,
    val tecnico: String,
    val fechaEntrega: String,
    val estado: String,
    val comentarios: String
)