package ru.technopark.vtelefeed

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi

class TgAuthViewModel : ViewModel() {

    private val _userPhoto = MutableLiveData<TdApi.ChatPhoto>()

    val authState: LiveData<TdApi.AuthorizationState?> = TgClient.authStateFlow.asLiveData()
    val userPhoto: LiveData<TdApi.ChatPhoto> = _userPhoto

    fun setPhoneNumber(number: String) {
        TgClient.setAuthNumber(number)
    }

    fun setWaitCode(code: String) {
        TgClient.checkAuthCode(code)
    }

    fun loadUserPhoto() {
        viewModelScope.launch {
            TgClient.loadUserFirstProfilePhoto()?.let { photo ->
                _userPhoto.value = photo
            }
        }
    }
}
