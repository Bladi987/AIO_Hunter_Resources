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
    var isDataLoaded = false
    val exception = MutableLiveData<String>()
    val user = MutableLiveData<Result<ArrayList<user>>>()
    val insertUser = MutableLiveData<Result<user>>()
    val updateUser = MutableLiveData<Result<user>>()
    val deleteUser = MutableLiveData<Result<String>>()
    val reset = MutableLiveData<Boolean>()
    val isloading = MutableLiveData<Boolean>()
    var getUserUseCase = getUserUseCase()
    var insertUserUseCase = insertUserUseCase()
    var updateUserUseCase = updateUserUseCase()
    var deleteUserUseCase = deleteUserUseCase()

    fun onCreate(urlid: urlId) {
        if (!isDataLoaded) {
            viewModelScope.launch {
                try {
                    isloading.postValue(true)
                    val response = getUserUseCase(urlid)
                    if (response.isSuccess) {
                        response.getOrNull()?.let { lista ->
                            user.postValue(Result.success(lista))
                            isDataLoaded = true
                        }
                    } else {
                        response.exceptionOrNull()?.let { ex ->
                            user.postValue(Result.failure(ex))
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

    fun onRefresh(urlid: urlId) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = getUserUseCase(urlid)
                if (response.isSuccess) {
                    response.getOrNull()?.let { lista ->
                        user.postValue(Result.success(lista))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        user.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun insertUser(urlid: urlId, user: user) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = insertUserUseCase(urlid, user)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        insertUser.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        insertUser.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun updateUser(urlid: urlId, user: user, action: String) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = updateUserUseCase(urlid, user)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        if (action == "reset") reset.postValue(true)
                        else updateUser.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        updateUser.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {
                exception.postValue(e.message)
            } finally {
                isloading.postValue(false)
            }
        }
    }

    fun deleteUser(urlid: urlId, user: user) {
        viewModelScope.launch {
            try {
                isloading.postValue(true)
                val response = deleteUserUseCase(urlid, user)
                if (response.isSuccess) {
                    response.getOrNull()?.let { registro ->
                        deleteUser.postValue(Result.success(registro))
                    }
                } else {
                    response.exceptionOrNull()?.let { ex ->
                        deleteUser.postValue(Result.failure(ex))
                    }
                }
            } catch (e: Exception) {

            } finally {
                isloading.postValue(false)
            }
        }
    }
}