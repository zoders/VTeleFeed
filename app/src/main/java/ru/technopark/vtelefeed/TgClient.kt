package ru.technopark.vtelefeed

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object TgClient {

    private const val API_ID = 8803623
    private const val API_HASH = "57cd1dc272b50a33d4060686f1f71d32"

    private lateinit var client: Client

    private val _authStateFlow: MutableStateFlow<TdApi.AuthorizationState?> =
        MutableStateFlow(null)

    lateinit var tgSource: TelegramDataSource

    var myUserId: Long? = null

    val clientFlow = callbackFlow<TdApi.Object> {

        client = Client.create(
            { obj ->
                trySend(obj)

                if (obj is TdApi.UpdateAuthorizationState) {
                    handleAuthStateUpdate(obj.authorizationState)
                }

                if (obj is TdApi.UpdateOption && obj.name == "my_id") {
                    myUserId = (obj.value as TdApi.OptionValueInteger).value
                }
            },
            { trySend(TdApi.Error(it.hashCode(), it.message)) },
            { trySend(TdApi.Error(it.hashCode(), it.message)) }
        )

        tgSource = TelegramDataSource(client)

        awaitClose { client.close() }
    }

    val authStateFlow: StateFlow<TdApi.AuthorizationState?> = _authStateFlow.asStateFlow()

    fun setAuthNumber(number: String) {
        client.send(
            TdApi.SetAuthenticationPhoneNumber(number, null),
            null, null
        )
    }

    fun checkAuthCode(code: String) {
        client.send(
            TdApi.CheckAuthenticationCode(code), null, null
        )
    }

    suspend fun loadUserFirstProfilePhoto(): TdApi.ChatPhoto? = suspendCoroutine { cont ->
        myUserId?.toInt()?.let { id ->
            client.send(
                TdApi.GetUserProfilePhotos(id, 0, 1),
                { obj ->
                    cont.resume((obj as TdApi.ChatPhotos).photos.firstOrNull())
                },
                { e ->
                    cont.resumeWithException(e)
                }
            )
        }
    }

    private fun handleAuthStateUpdate(state: TdApi.AuthorizationState) {
        _authStateFlow.value = state
        when (state) {
            is TdApi.AuthorizationStateWaitTdlibParameters -> setTdLibParameters()
            is TdApi.AuthorizationStateWaitEncryptionKey -> checkDbEncryptionKey()
        }
    }

    private fun setTdLibParameters() {
        val filesDir = File(
            VTeleFeedApplication.instance.filesDir.absolutePath,
            "tdlib"
        ).absolutePath

        client.send(
            TdApi.SetTdlibParameters(
                TdApi.TdlibParameters(
                    false,
                    filesDir,
                    null,
                    true,
                    true,
                    true,
                    false,
                    API_ID,
                    API_HASH,
                    "en",
                    "Android",
                    null,
                    "1.0",
                    true,
                    false
                )
            ),
            null,
            null
        )
    }

    private fun checkDbEncryptionKey() {
        client.send(TdApi.CheckDatabaseEncryptionKey(), null, null)
    }
}
