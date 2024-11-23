package com.kasolution.aiohunterresources.UI.Access.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.access.loginUseCase


import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    val Datauser = MutableLiveData<user>()
    val isloading = MutableLiveData<Boolean>()
    val access = MutableLiveData<Int>()
    var getDataUser = loginUseCase()


    fun onCreate(urlid: urlId,user:user) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response = getDataUser(urlid,user)
            if (response.id.isNotEmpty()) {
                //se evalua si el id no esta vacio
                if (response.password.isNotEmpty()){
                    //si tiene datos y el password es correcto dara como valor 1 acceso correcto
                    Datauser.postValue(response)
                    access.postValue(1)
                }else{
                    //si el campo password esta vacio necesita un reseteo y data el valor 2
                    Datauser.postValue(response)
                    access.postValue(2)
                }
            } else {
                //si no llega ningun dato los datos son incorrectos
                access.postValue(3)
            }
            isloading.postValue(false)
        }
    }
}