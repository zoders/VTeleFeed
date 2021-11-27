package ru.technopark.vtelefeed

import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import org.json.JSONException
import org.json.JSONObject

data class VKPost(
    val postID: Int = 0,
    val sourceID: Int = 0,
    val text: String = "",
    val date: Long = 0,
    var groupName: String = "",
    var groupPhoto: String = ""
) {
    companion object {
        fun parse(json: JSONObject): VKPost {
            try {
                val postID = json.getInt("post_id")
                val sourceID = -json.getInt("source_id")
                val text = json.getString("text")
                val date = json.getLong("date")
                return VKPost(postID, sourceID, text, date)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
