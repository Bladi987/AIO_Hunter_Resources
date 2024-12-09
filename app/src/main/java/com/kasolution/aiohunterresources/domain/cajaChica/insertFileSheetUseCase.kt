package com.kasolution.aiohunterresources.domain.cajaChica

import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica


class insertFileSheetUseCase() {
    private val repository= RepositoryCajaChica()
    suspend operator fun invoke(urlid: urlId,fileDetails: fileDetails): fileDetails {
        lateinit var dataFileSheet: fileDetails
        val response=repository.insertFileSheet(urlid,fileDetails).asJsonObject
        Log.i("BladiDev",response.toString())
        val data= response?.get("Resultado")?.asString
        if (data.toString()!="error" && data.toString()!="Duplicado"){
            val nombreReal = data
            dataFileSheet = fileDetails(nombreReal.toString(), nombreReal.toString())
        }else{
            dataFileSheet = fileDetails("", "")
        }
        return dataFileSheet
    }
}