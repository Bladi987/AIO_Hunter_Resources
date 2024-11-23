package com.kasolution.aiohunterresources.domain.user

import android.util.Log
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryAccess


class deleteUserUseCase() {
    private val repository = RepositoryAccess()
    suspend operator fun invoke(urlid: urlId, user: user): String {
        lateinit var responseId: String
        val response = repository.deleteUser(urlid, user).asJsonObject
        if (response != null) responseId = "done"
        else responseId = "error"
        return responseId
    }

}