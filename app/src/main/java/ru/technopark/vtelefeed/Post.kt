package ru.technopark.vtelefeed

import org.drinkless.td.libcore.telegram.TdApi

data class Post(
    val tgPost: TdApi.Message
)
