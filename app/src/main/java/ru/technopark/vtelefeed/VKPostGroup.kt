package ru.technopark.vtelefeed

import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import org.json.JSONException
import org.json.JSONObject

data class VKPostGroup(
    val sourceID: Int = 0,
    val groupName: String = "",
    val groupPhoto: String = ""

) {
    companion object {
        fun parse(json: JSONObject): VKPostGroup {
            try {
                val postID = json.getInt("id")
                val groupName = json.getString("name")
                val groupPhoto = json.getString("photo_100")
                return VKPostGroup(postID, groupName, groupPhoto)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}