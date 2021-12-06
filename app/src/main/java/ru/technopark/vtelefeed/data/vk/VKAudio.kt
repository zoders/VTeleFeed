package ru.technopark.vtelefeed.data.vk

import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONException
import org.json.JSONObject
@Serializable
@SerialName("VKAudio")
data class VKAudio(
    val id: Int = 0,
    val ownerId: Int = 0,
    val artist: String = "",
    val title: String = "",
    val duration: Int = 0,
    val url: String = "",
    val date: Long = 0
) {
    companion object {
        fun parse(json: JSONObject): VKAudio {
            try {
                val id = json.getInt("id")
                val ownerId = json.getInt("owner_id")
                val artist = json.getString("artist")
                val title = json.getString("title")
                val duration = json.getInt("duration")
                val url = json.getString("url")
                val date = json.getLong("date")
                return VKAudio(id, ownerId, artist, title, duration, url, date)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
