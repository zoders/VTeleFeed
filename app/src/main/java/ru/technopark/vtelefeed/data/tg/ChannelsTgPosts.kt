package ru.technopark.vtelefeed.data.tg

data class ChannelsTgPosts(
    val posts: List<TgPost>,
    val offset: Offset
)
