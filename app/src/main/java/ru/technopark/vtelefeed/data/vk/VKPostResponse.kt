package ru.technopark.vtelefeed.data.vk

import ru.technopark.vtelefeed.data.VKPost

data class VKPostResponse(
    val vkPosts: List<VKPost>,
    val nextFrom: String = ""
)
