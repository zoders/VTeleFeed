package ru.technopark.vtelefeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import org.drinkless.td.libcore.telegram.TdApi

class TgAuthViewModel : ViewModel() {

    val authState: LiveData<TdApi.AuthorizationState?> = TgClient.authStateFlow.asLiveData()

    fun setPhoneNumber(number: String) {
        TgClient.setAuthNumber(number)
    }

    fun setWaitCode(code: String) {
        TgClient.checkAuthCode(code)
    }
}
