package ru.technopark.vtelefeed.data.tg

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.shareIn
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.VTeleFeedApplication
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object TgClient {

    private const val API_ID = 8803623
    private const val API_HASH = "57cd1dc272b50a33d4060686f1f71d32"

    private var client: Client? = null

    private val _authStateFlow: MutableStateFlow<TdApi.AuthorizationState?> =
        MutableStateFlow(null)

    private val _myUserStateFlow: MutableStateFlow<TdApi.User?> =
        MutableStateFlow(null)

    private val clientFlows: MutableSharedFlow<Flow<TdApi.Object>> =
        MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    lateinit var tgSource: TelegramDataSource

    var myUserId: Long? = null

    val authStateFlow: StateFlow<TdApi.AuthorizationState?> = _authStateFlow.asStateFlow()
    val myUserStateFlow: StateFlow<TdApi.User?> = _myUserStateFlow.asStateFlow()

    val clientFlow: SharedFlow<TdApi.Object> =
        clientFlows
            .onSubscription { emit(newClientFlow()) }
            .flatMapLatest { it }
            .shareIn(VTeleFeedApplication.instance.applicationScope, SharingStarted.Lazily)

    suspend fun setAuthNumber(number: String): TdApi.Object = suspendCoroutine { cont ->
        client?.send(
            TdApi.SetAuthenticationPhoneNumber(number, null),
            { obj -> cont.resume(obj) },
            { e -> cont.resumeWithException(e) }
        )
    }

    suspend fun checkAuthCode(code: String): TdApi.Object = suspendCoroutine { cont ->
        client?.send(
            TdApi.CheckAuthenticationCode(code),
            { obj -> cont.resume(obj) },
            { e -> cont.resumeWithException(e) }
        )
    }

    suspend fun checkPassword(password: String): TdApi.Object = suspendCoroutine { cont ->
        client?.send(
            TdApi.CheckAuthenticationPassword(password),
            { obj -> cont.resume(obj) },
            { e -> cont.resumeWithException(e) }
        )
    }

    suspend fun loadUserPhoto(id: Int): TdApi.File? = suspendCoroutine { cont ->
        client?.send(
            TdApi.DownloadFile(
                id,
                Priorities.HIGH.toInt(),
                0,
                0,
                false
            ),
            { obj -> cont.resume(obj as? TdApi.File) },
            { e -> cont.resumeWithException(e) }
        )
    }

    suspend fun logOut(): TdApi.Object = suspendCoroutine { cont ->
        client?.send(
            TdApi.LogOut(),
            { obj -> cont.resume(obj) },
            { e -> cont.resumeWithException(e) }
        )
    }

    private fun handleAuthStateUpdate(state: TdApi.AuthorizationState) {
        _authStateFlow.value = state
        when (state) {
            is TdApi.AuthorizationStateWaitTdlibParameters -> setTdLibParameters()
            is TdApi.AuthorizationStateWaitEncryptionKey -> checkDbEncryptionKey()
            is TdApi.AuthorizationStateReady -> initMyUser()
            is TdApi.AuthorizationStateClosed -> clientFlows.tryEmit(newClientFlow())
        }
    }

    private fun setTdLibParameters() {
        val filesDir = File(
            VTeleFeedApplication.instance.applicationInfo.dataDir,
            "tdlib"
        ).absolutePath

        client?.send(
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
        client?.send(TdApi.CheckDatabaseEncryptionKey(), null, null)
    }

    private fun initMyUser() {
        client?.send(TdApi.GetMe()) { obj ->
            _myUserStateFlow.value = obj as? TdApi.User
        }
    }

    private fun newClientFlow(): Flow<TdApi.Object> = callbackFlow<TdApi.Object> {
        client = Client.create(
            { obj ->
                trySend(obj)
                if (obj is TdApi.UpdateAuthorizationState) {
                    handleAuthStateUpdate(obj.authorizationState)
                }
                if (obj is TdApi.UpdateOption && obj.name == "my_id") {
                    myUserId = (obj.value as? TdApi.OptionValueInteger)?.value
                }
            },
            { e -> trySend(TdApi.Error(e.hashCode(), e.message)) },
            { e -> trySend(TdApi.Error(e.hashCode(), e.message)) }
        )

        client?.let {
            tgSource = TelegramDataSource(it)
        }

        awaitClose {
            client?.close()
            _myUserStateFlow.value = null
        }
    }

    enum class Priorities {
        HIGH, MEDIUM, LOW;

        fun toInt() = ordinal + 1
    }
}
