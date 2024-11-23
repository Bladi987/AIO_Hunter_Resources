package com.kasolution.recursoshunter.UI.view.Home.Interfaces

interface DialogListener {
    fun onDataCollected(
        id:String,
        data: String,
        type: String,
        marca: String,
        modelo: String,
        comentarios: String,
        basico: String,
        otros: String,
        tecnico: String,
        aprobado: String,
        estado: String
    )
    fun onDataCollectedUpdate(
        id:String,
        data: String,
        type: String,
        marca: String,
        modelo: String,
        linkImage: String,
        comentarios: String,
        basico: String,
        otros: String,
        tecnico: String,
        aprobado: String,
        estado: String
    )
}