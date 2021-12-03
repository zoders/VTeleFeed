package ru.technopark.vtelefeed.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.technopark.vtelefeed.data.tg.TgPost

@Serializable
@Entity(tableName = "posts_table")
data class Post(
    val tgPost: TgPost
) {
    @PrimaryKey
    var id: Long = tgPost.id
    var date: Int = tgPost.date
}
