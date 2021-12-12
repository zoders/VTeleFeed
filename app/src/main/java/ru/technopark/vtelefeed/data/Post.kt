package ru.technopark.vtelefeed.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "posts_table")
data class Post(
    val innerPost: TgPost
) : BasePost() {
    @PrimaryKey
    override var id: String = innerPost.id.toString() + '_' + innerPost.chatId.toString()
    override var date: Int = innerPost.date
}
