package ru.technopark.vtelefeed.data.tg

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.drinkless.td.libcore.telegram.TdApi

@Serializable
class TgPost(
    @Contextual
    val message: TdApi.Message,
    @Contextual
    val chat: TdApi.Chat
)
