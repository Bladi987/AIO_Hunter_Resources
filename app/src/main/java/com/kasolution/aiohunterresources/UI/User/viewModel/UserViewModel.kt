package com.kasolution.aiohunterresources.UI.User.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.domain.user.deleteUserUseCase
import com.kasolution.aiohunterresources.domain.user.getUserUseCase
import com.kasolution.aiohunterresources.domain.user.insertUserUseCase
import com.kasolution.aiohunterresources.domain.user.updateUserUseCase
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    val user = MutableLiveData<ArrayList<user>>()
    val insertUser=MutableLiveData<user>()
    val updateUser=MutableLiveData<user>()
    val deleteUser=MutableLiveData<String>()
    val reset=MutableLiveData<Boolean>()
    val isloading = MutableLiveData<Boolean>()
    var getUserUseCase = getUserUseCase()
    var insertUserUseCase = insertUserUseCase()
    var updateUserUseCase = updateUserUseCase()
    var deleteUserUseCase = deleteUserUseCase()

    fun onCreate(urlid: urlId) {
        if (user.value.isNullOrEmpty()) {
            viewModelScope.launch {
                isloading.postValue(true)
                val response = getUserUseCase(urlid)
                if (response.isNotEmpty()) {
                    user.postValue(response)
                }
                isloading.postValue(false)
            }
        }
    }

    fun insertUser(urlid: urlId,user:user) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response=insertUserUseCase(urlid,user)
            insertUser.postValue(response)
            isloading.postValue(false)
        }
    }

    fun updateUser(urlid: urlId,user:user,action:String) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response=updateUserUseCase(urlid,user)
            if (action=="reset") reset.postValue(true)
            else updateUser.postValue(response)
            isloading.postValue(false)
        }
    }

    fun deleteUser(urlid: urlId,user:user) {
        viewModelScope.launch {
            isloading.postValue(true)
            val response=deleteUserUseCase(urlid,user)
            deleteUser.postValue(response)
            isloading.postValue(false)
        }
    }
}