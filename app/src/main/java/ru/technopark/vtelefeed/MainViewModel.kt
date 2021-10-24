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

    override fun onCleared() {
        disposable.dispose()
    }

    fun setupClient() {
        disposable.add(
            clientObservable
                .subscribe(
                    { update ->
                        if (update is TdApi.UpdateAuthorizationState) {
                            Log.i(TAG, update.authorizationState.javaClass.name)
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

    private fun log(e: Throwable) {
        Log.e(TAG, e.stackTraceToString(), e)
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}
