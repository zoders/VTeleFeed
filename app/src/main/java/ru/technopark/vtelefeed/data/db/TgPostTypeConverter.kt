package ru.technopark.vtelefeed.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.technopark.vtelefeed.data.Post

class TgPostTypeConverter {
    @TypeConverter
    fun postToString(post: Post): String {
        return json.encodeToString(post)
    }

    private val json = Json { encodeDefaults = true }

    @TypeConverter
    fun postFromString(postString: String): Post {
        return json.decodeFromString(postString)
    }
}
