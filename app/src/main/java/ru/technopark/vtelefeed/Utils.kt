package ru.technopark.vtelefeed

import io.reactivex.rxjava3.core.Single
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi

fun Client.sendSingle(query: TdApi.Function): Single<TdApi.Object> =
    Single.create { emitter ->
        send(
            query,
            { result -> emitter.onSuccess(result) },
            { e -> emitter.onError(e) }
        )
    }
