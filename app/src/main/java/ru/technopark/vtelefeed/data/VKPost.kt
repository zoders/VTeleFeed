package ru.technopark.vtelefeed.data

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import kotlinx.serialization.Serializable
import org.json.JSONException
import org.json.JSONObject
import ru.technopark.vtelefeed.data.vk.VKAudio
import ru.technopark.vtelefeed.data.vk.VKDoc
import ru.technopark.vtelefeed.data.vk.VKLink
import ru.technopark.vtelefeed.data.vk.VKPhoto
import ru.technopark.vtelefeed.data.vk.VKVideo
import kotlin.collections.ArrayList

@Serializable
@Entity(tableName = "vk_posts_table")
data class VKPost(
    @PrimaryKey
    override val id: String = "",
    val postID: Int = 0,
    val sourceID: Int = 0,
    val text: String = "",
    override val date: Int = 0,
    val reposts: Int = 0,
    val likes: Int = 0,
    val views: Int = 0,
    val comments: Int = 0,
    val photos: List<VKPhoto>? = null,
    val links: List<VKLink>? = null,
    val videos: List<VKVideo>? = null,
    val docs: List<VKDoc>? = null,
    val audios: List<VKAudio>? = null,
    var groupName: String = "",
    var groupPhoto: String = ""
) : BasePost() {
    companion object {
        fun parse(json: JSONObject): VKPost {
            try {
                val postId = json.getInt("post_id")
                val sourceID = -json.getInt("source_id")
                val id = postId.toString() + '_' + sourceID.toString()
                val text = json.getString("text")
                val date = json.getInt("date")
                val reposts = json.getJSONObject("reposts").getInt("count")
                val likes = json.getJSONObject("likes").getInt("count")
                val views = json.getJSONObject("views").getInt("count")
                val comments = json.getJSONObject("comments").getInt("count")
                val photosList = ArrayList<VKPhoto>()
                val linksList = ArrayList<VKLink>()
                val videosList = ArrayList<VKVideo>()
                val docsList = ArrayList<VKDoc>()
                val audiosList = ArrayList<VKAudio>()
                try {
                    val attachments = json.getJSONArray("attachments")
                    for (i in 0 until attachments.length()) {
                        val attachment = attachments.getJSONObject(i)
                        when (attachment.getString("type")) {
                            "photo" -> {
                                val photo = VKPhoto.parse(attachment.getJSONObject("photo"))
                                photosList.add(photo)
                            }
                            "link" -> {
                                val link = VKLink.parse(attachment.getJSONObject("link"))
                                linksList.add(link)
                            }
                            "video" -> {
                                val video = VKVideo.parse(attachment.getJSONObject("video"))
                                videosList.add(video)
                            }
                            "doc" -> {
                                val doc = VKDoc.parse(attachment.getJSONObject("doc"))
                                docsList.add(doc)
                            }
                            "audio" -> {
                                val audio = VKAudio.parse(attachment.getJSONObject("audio"))
                                audiosList.add(audio)
                            }
                        }
                    }
                }
                catch (e: JSONException) {
                    Log.i("VkPost", "There are no attachments in post $e")
                }
                return VKPost(
                    id, postId, sourceID, text, date, reposts, likes, views, comments,
                    photosList, linksList, videosList, docsList, audiosList
                )
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
