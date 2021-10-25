package ru.technopark.vtelefeed

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi


class MainViewModel : ViewModel() {

    private val disposable: CompositeDisposable = CompositeDisposable()

    private lateinit var client: Client

    private val clientObservable: Observable<TdApi.Object> = Observable.create { emitter ->
        client = Client.create(
            { update ->
                val a = 5
                emitter.onNext(update)
            },
            emitter::onError,
            emitter::onError
        )
    }

    init {
        setupClient()
    }

    override fun onCleared() {
        disposable.dispose()
    }

    private fun setupClient() {
        disposable.add(
            clientObservable
                .subscribe(
                    { update ->
                        if (update is TdApi.UpdateAuthorizationState &&
                            update.authorizationState is TdApi.AuthorizationStateReady
                        ) {
                            getChats()
                        }
                    },
                    { e -> Log.e(TAG, e.stackTraceToString(), e) }
                )
        )
        setTdLibParameters()
        setEncryptionKey()
//        setPhoneNumber()
//        setWaitCode()
    }

    private fun setTdLibParameters() {
        disposable.add(
            client.sendSingle(
                TdApi.SetTdlibParameters(
                    TdApi.TdlibParameters(
                        false,
                        "/sdcard/Android/data/ru.technopark.vtelefeed/files",
                        "/sdcard/Android/data/ru.technopark.vtelefeed/files",
                        false,
                        false,
                        false,
                        false,
                        7323533,
                        "00e5449a30bce5a038c18909391646e2",
                        "en-GB",
                        "Samsung",
                        "Android 11",
                        "1",
                        false,
                        true
                    )
                )
            )
                .subscribe(
                    { result -> },
                    ::log
                )
        )
    }

    private fun setEncryptionKey() {
        disposable.add(
            client.sendSingle(TdApi.SetDatabaseEncryptionKey())
                .subscribe(
                    { result -> },
                    ::log
                )
        )
    }

    private fun setPhoneNumber() {
        disposable.add(
            client.sendSingle(
                TdApi.SetAuthenticationPhoneNumber(
                    "79778483132",
                    TdApi.PhoneNumberAuthenticationSettings(false, true, false)
                )
            )
                .subscribe(
                    { result -> },
                    ::log
                )
        )
    }

    private fun setWaitCode() {
        disposable.add(
            client.sendSingle(TdApi.CheckAuthenticationCode("84805"))
                .subscribe(
                    { result -> },
                    ::log
                )
        )
    }

    private fun getChats(offsetOrder: Long = Long.MAX_VALUE, offsetChatId: Long = 0) {
        var haveMoreChats: Boolean = false

        disposable.add(
            client.sendSingle(TdApi.GetChats(TdApi.ChatListMain(), offsetOrder, offsetChatId, 3))
                .flatMapObservable { chats ->
                    Observable.fromArray<Long>(*(chats as TdApi.Chats).chatIds.toTypedArray())
                }
                .flatMap { id ->
                    client.sendSingle(TdApi.GetChat(id)).toObservable()
                }
                .toList()
                .subscribe(
                    { result ->
                        val chats = result as List<TdApi.Chat>
                        haveMoreChats = chats.isNotEmpty()
                        if (haveMoreChats) {
                            val offset = chats.last().id
                            val order = chats.last().positions.last().order
                            getChats(order, offset)
                        }

                        chats.forEach { chat ->
                            if (chat.title == "Айдем") {
                                getMessages(chat.id)
                            }
                        }

//                        Log.i(TAG, chats.map { it.title }.toString())
                    },
                    ::log
                )
        )
    }


    private fun getMessages(chatId: Long, fromMessageId: Long = 0, offset: Int = 0) {
        var haveMoreMessages: Boolean = false

        disposable.add(
            client.sendSingle(
                TdApi.GetChatHistory(chatId, fromMessageId, offset, 10, false)
            )
                .subscribe(
                    { result ->
                        val messages = (result as TdApi.Messages).messages.toList()
                        haveMoreMessages = messages.isNotEmpty()
                        if (haveMoreMessages) {
                            getMessages(chatId, messages.last().id)
                        }
                        Log.i(TAG, messages.map { it.content } .toString())
                    },
                    ::log
                )
        )
    }

    private fun log(e: Throwable) {
        Log.e(TAG, e.stackTraceToString(), e)
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}
