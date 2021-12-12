package ru.technopark.vtelefeed.data

import kotlinx.serialization.Serializable

@Serializable
sealed class BasePost {
    abstract val id: String
    abstract val date: Int
}
