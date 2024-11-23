package com.kasolution.aiohunterresources.UI.CajaChica.interfaces

import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register

interface DialogListener {
    fun onDataCollected(
        register: register
    )
    fun onDataCollectedUpdate(
        register: register
    )
}
