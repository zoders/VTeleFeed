package ru.technopark.vtelefeed

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi

object TgClient {

    private lateinit var client: Client

    lateinit var tgSource: TelegramDataSource


    val clientFlow = callbackFlow<TdApi.Object> {

        client = Client.create(
            { obj ->
                trySend(obj)
                handleUpdate(obj)
            },
            { trySend(TdApi.Error(it.hashCode(), it.message)) },
            { trySend(TdApi.Error(it.hashCode(), it.message)) }
        )

        tgSource = TelegramDataSource(client)

        awaitClose { client.close() }
    }

    private fun handleUpdate(obj: TdApi.Object) {
        if (obj is TdApi.UpdateAuthorizationState) {
            when (obj.authorizationState) {
                is TdApi.AuthorizationStateWaitTdlibParameters -> setTdLibParameters()
                is TdApi.AuthorizationStateWaitEncryptionKey -> checkDbEncryptionKey()
            }
        }
    }

    private fun setTdLibParameters() {
        client.send(
            TdApi.SetTdlibParameters(
                TdApi.TdlibParameters(
                    false,
                    "/sdcard/Android/data/ru.technopark.vtelefeed/files",
                    "/sdcard/Android/data/ru.technopark.vtelefeed/files",
                    true,
                    true,
                    true,
                    false,
                    8803623,
                    "57cd1dc272b50a33d4060686f1f71d32",
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

    private fun setAuthNumber() {
        client.send(
            TdApi.SetAuthenticationPhoneNumber("79778483132", null),
            null, null
        )
    }
}
