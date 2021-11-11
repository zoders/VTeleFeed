package ru.technopark.vtelefeed

import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import org.json.JSONException
import org.json.JSONObject

data class VKPost(
    val postID: Int = 0,
    val sourceID: Int = 0,
    val attachments: List<Attachment> = emptyList(),
    val text: String = "",
    val date: Long = 0) //unixtime
//    val comments: Int = 0,
//    val likes: Int = 0,
//    val reposts: Int = 0,
//    val views: Int = 0)
    {
    companion object {
        fun parse(json: JSONObject): VKPost {
            try {
                val postID = json.getInt("post_id")
                val sourceID = json.getInt("source_id")
                val text = json.getString("text")
                val date = json.getLong("date")
                val ja = json.optJSONArray("attachments")
                val attachments = ArrayList<Attachment>(ja.length())
                for (i in 0 until ja.length()) {
                    val attachment = Attachment.parse(ja.getJSONObject(i))
                    attachments.add(attachment)
                }
                return VKPost(postID, sourceID, attachments, text, date)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}