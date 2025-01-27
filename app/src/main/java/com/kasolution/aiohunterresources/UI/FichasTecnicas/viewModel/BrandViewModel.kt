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
    var isDataLoaded = false
    val exception = MutableLiveData<String>()
    val BrandModel = MutableLiveData<Result<ArrayList<Brand>>>()
    val isloading = MutableLiveData<Boolean>()
    var getBrandUseCase = getBrandUseCase()

    fun onCreate(urlId: urlId) {
        if (!isDataLoaded) {
            viewModelScope.launch {
                try {
                    isloading.postValue(true)
                    val response = getBrandUseCase(urlId)
                    if(response.isSuccess){
                        response.getOrNull()?.let { lista ->
                            BrandModel.postValue(Result.success(lista))
                            isDataLoaded = true
                        }
                    }else{
                        response.exceptionOrNull()?.let { ex ->
                            BrandModel.postValue(Result.failure(ex))
                        }
                    }
                }catch (e: Exception){
                    exception.postValue(e.message)
                }finally {
                    isloading.postValue(false)
                }
            }
        }
    }

    fun onRefresh(urlId: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getBrandUseCase(urlId)
                if(response.isSuccess){
                    response.getOrNull()?.let { lista ->
                        BrandModel.postValue(Result.success(lista))
                    }
                }else{
                    response.exceptionOrNull()?.let { ex ->
                        BrandModel.postValue(Result.failure(ex))
                    }
                }
            }catch (e: Exception){
                exception.postValue(e.message)
            }finally {
                isloading.postValue(false)
            }
        }
    }
}