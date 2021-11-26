package ru.technopark.vtelefeed

import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.utils.Loading

class TgAuthViewModel : ViewModel() {

    private val _userPhoto = MutableLiveData<TdApi.File>()

    val authState: LiveData<TdApi.AuthorizationState?> = TgClient.authStateFlow.asLiveData()
    val userPhoto: LiveData<TdApi.File> = _userPhoto

    init {
        viewModelScope.launch {
            TgClient.clientFlow.collect { obj ->
                if (obj is TdApi.UpdateFile
                    && obj.file.id == _userPhoto.value?.id
                    && obj.file.local.isDownloadingCompleted
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
                    _userPhoto.value = localFile
                }
            }
        }
    }

    fun setPhoneNumber(number: String) = liveData<TdApi.Object> {
        emit(Loading)
        emit(TgClient.setAuthNumber(number))
    }

    fun setWaitCode(code: String) = liveData<TdApi.Object> {
        emit(Loading)
        emit(TgClient.checkAuthCode(code))
    }

    companion object {
        private const val TAG = "TgAuthViewModel"
    }
}
