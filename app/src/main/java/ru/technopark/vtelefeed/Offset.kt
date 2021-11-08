package ru.technopark.vtelefeed

data class Offset(
    val date: Int = 0,
    val chatId: Long = 0L,
    val messageId: Long = 0L
)
