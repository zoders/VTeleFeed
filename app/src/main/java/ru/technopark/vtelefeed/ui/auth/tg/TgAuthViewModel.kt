package ru.technopark.vtelefeed.ui.auth.tg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.data.tg.TgClient
import ru.technopark.vtelefeed.utils.Loading

class TgAuthViewModel : ViewModel() {

    private val _userPhoto = MutableLiveData<TdApi.File>()
    private val _phoneNumber = MutableLiveData<TdApi.Object>()
    private val _authCode = MutableLiveData<TdApi.Object>()
    private val _password = MutableLiveData<TdApi.Object>()
    private val _logOut = MutableLiveData<TdApi.Object>()
    private val _snackBars = MutableSharedFlow<String>()

    val authState: LiveData<TdApi.AuthorizationState?> = TgClient.authStateFlow.asLiveData()
    val user: LiveData<TdApi.User?> = TgClient.myUserStateFlow.asLiveData()
    val userPhoto: LiveData<TdApi.File> = _userPhoto
    val phoneNumber: LiveData<TdApi.Object> = _phoneNumber
    val authCode: LiveData<TdApi.Object> = _authCode
    val password: LiveData<TdApi.Object> = _password
    val logOut: LiveData<TdApi.Object> = _logOut

    val snackBars: SharedFlow<String> = _snackBars.asSharedFlow()

    init {
        viewModelScope.launch {
            TgClient.clientFlow.collect { obj ->
                if (obj is TdApi.UpdateFile &&
                    obj.file.id == _userPhoto.value?.id &&
                    obj.file.local.isDownloadingCompleted
                ) {
                    _userPhoto.value = obj.file
                }
            }
        }

        viewModelScope.launch {
            TgClient.myUserStateFlow.collect { user ->
                user?.profilePhoto?.let { photo ->
                    val localFile = photo.big?.takeIf { it.local.isDownloadingCompleted }
                        ?: TgClient.loadUserPhoto(photo.big.id)
                    localFile?.let {
                        _userPhoto.value = it
                    }
                }
            }
        }
    }

    fun setPhoneNumber(number: String) {
        viewModelScope.launch {
            _phoneNumber.value = Loading
            _phoneNumber.value = TgClient.setAuthNumber(number)
        }
    }

    fun setWaitCode(code: String) {
        viewModelScope.launch {
            _authCode.value = Loading
            _authCode.value = TgClient.checkAuthCode(code)
        }
    }

    fun setPassword(password: String) {
        viewModelScope.launch {
            _password.value = Loading
            _password.value = TgClient.checkPassword(password)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            _logOut.value = Loading
            _logOut.value = TgClient.logOut()
        }
    }

    fun onSnackBar(message: String) {
        viewModelScope.launch {
            _snackBars.emit(message)
        }
    }

    companion object {
        private const val TAG = "TgAuthViewModel"
    }
}
