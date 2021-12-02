package ru.technopark.vtelefeed.data.vk

data class VKPostResponse(
    val vkPosts: List<VKPost>,
    val nextFrom: String = ""
)
