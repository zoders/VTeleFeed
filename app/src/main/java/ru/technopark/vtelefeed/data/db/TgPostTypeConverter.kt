package ru.technopark.vtelefeed.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.technopark.vtelefeed.data.tg.TgPost

class TgPostTypeConverter {

    @TypeConverter
    fun tgPostToString(tgPost: TgPost): String {
        return Json.encodeToString(tgPost)
    }

    @TypeConverter
    fun tgPostFromString(tgPostString: String): TgPost {
        return Json.decodeFromString(tgPostString)
    }

}
