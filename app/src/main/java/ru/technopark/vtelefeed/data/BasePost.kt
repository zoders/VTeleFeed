package ru.technopark.vtelefeed.data

import androidx.room.PrimaryKey
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
sealed class BasePost {
    abstract val id: String
    abstract val date: Int
}