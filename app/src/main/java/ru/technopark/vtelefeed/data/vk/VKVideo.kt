package ru.technopark.vtelefeed.data.vk

import android.util.Log
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONException
import org.json.JSONObject

// url = "https://vk.com/video?z=video${ownerId}_${id}"
@Serializable
@SerialName("VKVideo")
data class VKVideo(
    val id: Int = 0,
    val ownerId: Int = 0,
    val date: Long = 0,
    val image: String = "",
    val title: String = "",
    val description: String = ""
) {
    companion object {
        fun parse(json: JSONObject): VKVideo {
            try {
                val id = json.getInt("id")
                val ownerId = json.getInt("owner_id")
                val date = json.getLong("date")
                val images = json.getJSONArray("image")
                val image = images.getJSONObject(images.length() - 1).getString("url")
                val title = json.getString("title")
                var description = ""
                try {
                    description = json.getString("description")
                }
                catch (e: JSONException) {
                    Log.i("VKVideo", "There is no description in video $e")
                }
                return VKVideo(id, ownerId, date, image, title, description)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
