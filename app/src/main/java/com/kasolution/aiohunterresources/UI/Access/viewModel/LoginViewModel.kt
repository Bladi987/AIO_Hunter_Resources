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
    val exception = MutableLiveData<String>()
    val Datauser = MutableLiveData<Result<user>>()
    val isloading = MutableLiveData<Boolean>()
    val access = MutableLiveData<Int>()
    var getDataUser = loginUseCase()


    fun onCreate(urlid: urlId, user: user) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getDataUser(urlid, user)
                if (response.isSuccess) {
                    response.getOrNull()?.let { dataUser ->
                        if (dataUser.id.isNotEmpty()) {
                            //se evalua si el id no esta vacio
                            if (dataUser.password.isNotEmpty()) {
                                //si tiene datos y el password es correcto dara como valor 1 acceso correcto
                                Datauser.postValue(Result.success(dataUser))
                                access.postValue(1)
                            } else {
                                //si el campo password esta vacio necesita un reseteo y data el valor 2
                                Datauser.postValue(Result.success(dataUser))
                                access.postValue(2)
                            }
                        } else {
                            //si no llega ningun dato los datos son incorrectos
                            access.postValue(3)
                        }
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        Datauser.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }
}