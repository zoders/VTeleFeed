package ru.technopark.vtelefeed.data.vk

import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONException
import org.json.JSONObject
@Serializable
@SerialName("VKDoc")
data class VKDoc(
    val id: Int = 0,
    val ownerId: Int = 0,
    val title: String = "",
    val size: Int = 0,
    val ext: String = "",
    val url: String = "",
    val date: Long = 0,
    val type: Int = 0
) {
    companion object {
        fun parse(json: JSONObject): VKDoc {
            try {
                val id = json.getInt("id")
                val ownerId = json.getInt("owner_id")
                val title = json.getString("title")
                val size = json.getInt("size")
                val ext = json.getString("ext")
                val url = json.getString("url")
                val date = json.getLong("date")
                val type = json.getInt("type")
                return VKDoc(id, ownerId, title, size, ext, url, date, type)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
