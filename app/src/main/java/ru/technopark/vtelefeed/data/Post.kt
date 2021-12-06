package ru.technopark.vtelefeed.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "posts_table")
data class Post(
    val innerPost: BasePost
) {
    @PrimaryKey
    var id: Long = innerPost.id
    var date: Int = innerPost.date
}
