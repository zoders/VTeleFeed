package ru.technopark.vtelefeed.data.tg

import org.drinkless.td.libcore.telegram.TdApi

class TgPost(
    val message: TdApi.Message,
    val chat: TdApi.Chat
)