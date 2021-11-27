package ru.technopark.vtelefeed

data class VKPostResponse(
    val vkPosts: List<VKPost>,
    val nextFrom: String = ""
)
