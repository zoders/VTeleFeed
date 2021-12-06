package ru.technopark.vtelefeed.data.vk

import android.util.Log
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONException
import org.json.JSONObject
@Serializable
@SerialName("VKLink")
data class VKLink(
    val url: String = "",
    val title: String = "",
    val description: String = ""
) {
    companion object {
        fun parse(json: JSONObject): VKLink {
            try {
                val url = json.getString("url")
                val title = json.getString("title")
                var description = ""
                try {
                    description = json.getString("description")
                }
                catch (e: JSONException) {
                    Log.i("VKLink", "There is no description in link $e")
                }

                return VKLink(url, title, description)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
