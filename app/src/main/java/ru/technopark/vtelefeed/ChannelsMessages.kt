package ru.technopark.vtelefeed

import org.drinkless.td.libcore.telegram.TdApi

data class ChannelsMessages(
    val messages: List<TdApi.Message>,
    val offsetMessage: TdApi.Message
)
