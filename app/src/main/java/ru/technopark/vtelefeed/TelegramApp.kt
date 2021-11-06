package ru.technopark.vtelefeed

import java.io.File
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi

class TelegramApp constructor(private val callback: Callback?) {
    private var client: Client? = null
    private var appDir: String = ""
    private var authorizationState: TdApi.AuthorizationState? = null
    private var phoneNumber: String = ""
    private var verificationCode: String = ""
    fun createClient() {
        client = Client.create(callback, null, null)
        //Client.create(callback, null, null)
    }

    fun setAppDir(appDir: String) {
        this.appDir = appDir
    }

    fun setPhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun getPhoneNumber() : String {
        return this.phoneNumber
    }

    fun setVerificationCode(verificationCode: String) {
        this.verificationCode = verificationCode
    }

    fun getVerificationCode() : String {
        return this.verificationCode
    }

    fun changeAuthorizationState() {
        client?.send(TdApi.GetAuthorizationState()) { obj ->
            if (obj is TdApi.AuthorizationState) {
                onAuthorizationStateUpdated(obj)
            }
        }
    }

    fun logOut() {
        phoneNumber = ""
        verificationCode = ""
        client?.send(TdApi.LogOut(), null, null)
        changeAuthorizationState()
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
                if (phoneNumber != "") {
                    client?.send(
                        TdApi.SetAuthenticationPhoneNumber(phoneNumber, null),
                        null,
                        null
                    )
                }
            }
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> {
                if (verificationCode != "") {
                    client?.send(
                        TdApi.CheckAuthenticationCode(verificationCode),
                        null,
                        null
                    )
                }

            }
            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> {
                // Logging out
            }
            TdApi.AuthorizationStateClosing.CONSTRUCTOR -> {
                // Closing
            }
            TdApi.AuthorizationStateClosed.CONSTRUCTOR -> {
                // Closed
                client = Client.create(callback, null, null)
            }
            else -> {}
        }
        this.authorizationState = authorizationState
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

    interface Callback : Client.ResultHandler {
        override fun onResult(obj: TdApi.Object)
    }

    companion object {
        private const val apiId = 8803623
        private const val apiHash = "57cd1dc272b50a33d4060686f1f71d32"
    }
}
