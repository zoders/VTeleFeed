package ru.technopark.vtelefeed.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.technopark.vtelefeed.data.vk.VKAudio
import ru.technopark.vtelefeed.data.vk.VKDoc
import ru.technopark.vtelefeed.data.vk.VKLink
import ru.technopark.vtelefeed.data.vk.VKPhoto
import ru.technopark.vtelefeed.data.vk.VKVideo

class VKPostTypeConverter {
    @TypeConverter
    fun vkLinksToString(vkLinks: List<VKLink>): String {
        return Json.encodeToString(vkLinks)
    }

    @TypeConverter
    fun vkLinksFromString(vkLinksString: String): List<VKLink> {
        return Json.decodeFromString(vkLinksString)
    }

    @TypeConverter
    fun vkDocsToString(vkDocs: List<VKDoc>): String {
        return Json.encodeToString(vkDocs)
    }

    @TypeConverter
    fun vkDocsFromString(vkDocsString: String): List<VKDoc> {
        return Json.decodeFromString(vkDocsString)
    }

    @TypeConverter
    fun vkPhotosToString(vkPhotos: List<VKPhoto>): String {
        return Json.encodeToString(vkPhotos)
    }

    @TypeConverter
    fun vkPhotosFromString(vkPhotosString: String): List<VKPhoto> {
        return Json.decodeFromString(vkPhotosString)
    }

    @TypeConverter
    fun vkAudiosToString(vkAudios: List<VKAudio>): String {
        return Json.encodeToString(vkAudios)
    }

    @TypeConverter
    fun vkAudiosFromString(vkAudiosString: String): List<VKAudio> {
        return Json.decodeFromString(vkAudiosString)
    }

    @TypeConverter
    fun vkVideosToString(vkVideos: List<VKVideo>): String {
        return Json.encodeToString(vkVideos)
    }

    @TypeConverter
    fun vkVideosFromString(vkVideosString: String): List<VKVideo> {
        return Json.decodeFromString(vkVideosString)
    }
}