package ru.technopark.vtelefeed

import android.text.TextUtils
import android.util.Log
import java.io.File
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi

class TelegramClient {
    companion object {
        private const val apiId = 8803623
        private const val apiHash = "57cd1dc272b50a33d4060686f1f71d32"
        private const val requestPhotoPriority = 10
        private var telegram: TelegramClient? = null
        val instance: TelegramClient
            get() {
                if (telegram == null) {
                    telegram = TelegramClient()
                }
                return telegram!!
            }
        const val TAG = "TelegramClient"
    }
    var appDir: String = ""
        get() = field
        set(value) { field = value }
    var user: TdApi.User? = null
    private var client: Client? = null
    private var authorizationState: TdApi.AuthorizationState? = null
    private var authorizationRequestHandler: UpdateHandler? = null
    private var telegramAuthorizationRequestHandler: TelegramAuthorizationRequestHandler? = null

    // Сраный Detekt не дает сделать больше 10 методов. Это так критично?
    /*
    fun close() {
        client!!.send(TdApi.Close(), null, null)
    }

    fun logOut() {
        client?.send(TdApi.LogOut(), null, null)
        changeAuthorizationState()
    }
    */

    fun createClient() {
        authorizationRequestHandler = UpdateHandler()
        client = Client.create(
            authorizationRequestHandler,
            null,
            null
        )
    }

    fun changeAuthorizationState() {
        client?.send(TdApi.GetAuthorizationState()) { obj ->
            if (obj is TdApi.AuthorizationState) {
                onAuthorizationStateUpdated(obj)
            }
        }
    }

    inner class TelegramAuthorizationRequestHandler(val telegramAuthorizationRequestListener: TelegramAuthorizationRequestListener) {
        fun applyAuthenticationParameter(
            parameterType: TelegramAuthenticationParameterType,
            parameterValue: String
        ) {
            if (!TextUtils.isEmpty(parameterValue)) {
                when (parameterType) {
                    TelegramAuthenticationParameterType.PHONE_NUMBER -> client!!.send(
                        TdApi.SetAuthenticationPhoneNumber(
                            parameterValue,
                            TdApi.PhoneNumberAuthenticationSettings(
                                false,
                                false,
                                false
                            )
                        ),
                        null
                    )
                    TelegramAuthenticationParameterType.CODE -> client!!.send(
                        TdApi.CheckAuthenticationCode(parameterValue),
                        null
                    )
                }
            }
        }
    }

    fun setTelegramAuthorizationRequestHandler(
        telegramAuthorizationRequestListener: TelegramAuthorizationRequestListener
    ): TelegramAuthorizationRequestHandler {
        val handler = TelegramAuthorizationRequestHandler(telegramAuthorizationRequestListener)
        this.telegramAuthorizationRequestHandler = handler
        return handler
    }

    fun onAuthorizationStateUpdated(authorizationState: TdApi.AuthorizationState?) {
        if (authorizationState != null) {
            this.authorizationState = authorizationState
        }
        when (this.authorizationState?.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> {
                val parameters = TdApi.TdlibParameters()
                parameters.databaseDirectory = File(appDir, "tdlib").absolutePath
                parameters.useMessageDatabase = true
                parameters.useSecretChats = true
                parameters.apiId = apiId
                parameters.apiHash = apiHash
                parameters.systemLanguageCode = "en"
                parameters.deviceModel = "Android"
                parameters.applicationVersion = "1.0"
                parameters.enableStorageOptimizer = true
                client?.send(TdApi.SetTdlibParameters(parameters), null)
            }
            TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR -> client?.send(
                TdApi.CheckDatabaseEncryptionKey(),
                null
            )
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> {
                telegramAuthorizationRequestHandler?.telegramAuthorizationRequestListener
                    ?.onRequestTelegramAuthenticationParameter(
                        TelegramAuthenticationParameterType.PHONE_NUMBER
                    )
            }
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> {
                telegramAuthorizationRequestHandler?.telegramAuthorizationRequestListener
                    ?.onRequestTelegramAuthenticationParameter(
                        TelegramAuthenticationParameterType.CODE
                    )
            }
            TdApi.AuthorizationStateReady.CONSTRUCTOR -> {
                requestCurrentUser()
                telegramAuthorizationRequestHandler?.telegramAuthorizationRequestListener
                    ?.onRequestTelegramAuthenticationParameter(
                        TelegramAuthenticationParameterType.READY
                    )
            }
            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> {
                // Logging out
            }
            TdApi.AuthorizationStateClosing.CONSTRUCTOR -> {
                // Closing
            }
            TdApi.AuthorizationStateClosed.CONSTRUCTOR -> {
                // Closed
                client = Client.create(
                    authorizationRequestHandler,
                    null,
                    null
                )
            }
            else -> {}
        }
        this.authorizationState = authorizationState
    }

    enum class TelegramAuthenticationParameterType {
        PHONE_NUMBER,
        CODE,
        READY
    }

    enum class TelegramAuthorizationState {
        UNKNOWN,
        WAIT_PARAMETERS,
        WAIT_PHONE_NUMBER,
        WAIT_CODE,
        WAIT_PASSWORD,
        READY,
        LOGGING_OUT,
        CLOSING,
        CLOSED
    }

    fun getTelegramAuthorizationState(): TelegramAuthorizationState {
        val authorizationState = this.authorizationState
            ?: return TelegramAuthorizationState.UNKNOWN
        return when (authorizationState.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR ->
                TelegramAuthorizationState.WAIT_PARAMETERS
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR ->
                TelegramAuthorizationState.WAIT_PHONE_NUMBER
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR ->
                TelegramAuthorizationState.WAIT_CODE
            TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR ->
                TelegramAuthorizationState.WAIT_PASSWORD
            TdApi.AuthorizationStateReady.CONSTRUCTOR ->
                TelegramAuthorizationState.READY
            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR ->
                TelegramAuthorizationState.LOGGING_OUT
            TdApi.AuthorizationStateClosing.CONSTRUCTOR ->
                TelegramAuthorizationState.CLOSING
            TdApi.AuthorizationStateClosed.CONSTRUCTOR ->
                TelegramAuthorizationState.CLOSED
            else ->
                TelegramAuthorizationState.UNKNOWN
        }
    }

    fun requestCurrentUser() {
        client?.send(TdApi.GetMe()) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    Log.e(TAG, error.message)
                }
                TdApi.User.CONSTRUCTOR -> {
                    this.user = obj as TdApi.User
                }
            }
        }
    }

    private fun requestUserPhoto(user: TdApi.User) {
        val remotePhoto = user.profilePhoto?.big?.remote
        if (remotePhoto != null && remotePhoto.id.isNotEmpty()) {
            client!!.send(TdApi.GetRemoteFile(remotePhoto.id, null)) { obj ->
                when (obj.constructor) {
                    TdApi.Error.CONSTRUCTOR -> {
                        val error = obj as TdApi.Error
                        Log.e(TAG, error.message)
                    }
                    TdApi.File.CONSTRUCTOR -> {
                        val file = obj as TdApi.File
                        client!!.send(
                            TdApi.DownloadFile(file.id, requestPhotoPriority, 0, 0, true),
                            null,
                            null
                        )
                    }
                }
            }
        }
    }

    private fun hasLocalUserPhoto(user: TdApi.User): Boolean {
        val localPhoto = user.profilePhoto?.big?.local
        return if (localPhoto != null) {
            localPhoto.canBeDownloaded && localPhoto.isDownloadingCompleted && localPhoto.path.isNotEmpty()
        } else {
            false
        }
    }

    private fun hasRemoteUserPhoto(user: TdApi.User): Boolean {
        val remotePhoto = user.profilePhoto?.big?.remote
        return remotePhoto?.id?.isNotEmpty() ?: false
    }

    fun getUserPhotoPath(user: TdApi.User?) = when {
        user == null -> null
        hasLocalUserPhoto(user) -> user.profilePhoto?.big?.local?.path
        else -> {
            if (hasRemoteUserPhoto(user)) {
                requestUserPhoto(user)
            }
            null
        }
    }

    interface TelegramAuthorizationRequestListener {
        fun onRequestTelegramAuthenticationParameter(
            telegramAuthenticationParameterType: TelegramAuthenticationParameterType
        )
        fun onTelegramAuthorizationRequestError(code: Int, message: String)
    }

    open inner class UpdateHandler : Client.ResultHandler {
        override fun onResult(obj: TdApi.Object) {
            when (obj.constructor) {
                TdApi.UpdateAuthorizationState.CONSTRUCTOR ->
                    onAuthorizationStateUpdated(
                        (obj as TdApi.UpdateAuthorizationState).authorizationState
                    )
            }
        }
    }
}
