package ru.technopark.vtelefeed.data.vk

import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONException
import org.json.JSONObject
@Serializable
@SerialName("VKPhoto")
data class VKPhoto(
    val id: Int = 0,
    val text: String = "",
    val date: Long = 0,
    val url: String = "",
    val width: Int = 0,
    val height: Int = 0
) {
    companion object {
        fun parse(json: JSONObject): VKPhoto {
            try {
                val id = json.getInt("id")
                val text = json.getString("text")
                val date = json.getLong("date")
                val sizes = json.getJSONArray("sizes")
                val url = sizes.getJSONObject(sizes.length() - 1).getString("url")
                val width = sizes.getJSONObject(sizes.length() - 1).getInt("width")
                val height = sizes.getJSONObject(sizes.length() - 1).getInt("height")
                return VKPhoto(id, text, date, url, width, height)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
