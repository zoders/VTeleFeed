package ru.technopark.vtelefeed.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.technopark.vtelefeed.data.tg.TgPost

@Serializable
@Entity(tableName = "posts_table", primaryKeys = ["id", "id2"])
data class Post(
    val tgPost: TgPost
) {
    var id: Long = tgPost.id
    var id2: Long = tgPost.chatId
    var date: Int = tgPost.date
}
