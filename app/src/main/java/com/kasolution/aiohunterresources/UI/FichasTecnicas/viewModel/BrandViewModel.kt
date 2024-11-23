package com.kasolution.aiohunterresources.UI.FichasTecnicas.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.fichasTecnicas.getBrandUseCase
import kotlinx.coroutines.launch


class BrandViewModel : ViewModel() {
    val BrandModel = MutableLiveData<ArrayList<Brand>>()
    val isloading = MutableLiveData<Boolean>()
    var getBrandUseCase = getBrandUseCase()

    fun onCreate(urlId: urlId) {
        if (BrandModel.value.isNullOrEmpty()) {
            viewModelScope.launch {
                isloading.postValue(true)
                val response = getBrandUseCase(urlId)
                if (response.isNotEmpty()) {
                    BrandModel.postValue(response)
                }
                isloading.postValue(false)
            }
        }
    }

    fun onRefresh(urlId: urlId) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = getBrandUseCase(urlId)
            if (response.isNotEmpty()) {
                BrandModel.postValue(response)
            }
            isloading.postValue(false)
        }
    }
}