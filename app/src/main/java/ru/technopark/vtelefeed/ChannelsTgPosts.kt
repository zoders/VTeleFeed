package ru.technopark.vtelefeed

data class ChannelsTgPosts(
    val posts: List<TgPost>,
    val offset: Offset
)
