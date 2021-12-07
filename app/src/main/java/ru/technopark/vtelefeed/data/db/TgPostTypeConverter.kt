package ru.technopark.vtelefeed.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.TgPost

class TgPostTypeConverter {
    @TypeConverter
    fun postToString(post: TgPost): String {
        return json.encodeToString(post)
    }

    private val json = Json { encodeDefaults = true }

    @TypeConverter
    fun postFromString(postString: String): TgPost {
        return json.decodeFromString(postString)
    }
}
