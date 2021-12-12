package ru.technopark.vtelefeed.data.vk

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject
import ru.technopark.vtelefeed.data.VKPost
import java.util.concurrent.ConcurrentHashMap

const val DEFAULT_COUNT = 20

class VKNewsfeedCommand(
    private val count: Int = DEFAULT_COUNT,
    private var startFrom: String = ""
) : ApiCommand<VKPostResponse>() {
    override fun onExecute(manager: VKApiManager): VKPostResponse {
        val call = VKMethodCall.Builder()
            .method("newsfeed.get")
            .args("filters", "post")
            .args("source_ids", "groups")
            .args("count", count)
            .args("start_from", startFrom)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }

    private class ResponseApiParser : VKApiResponseParser<VKPostResponse> {
        private fun getJSONArray(response: String, arrayName: String) = JSONObject(response)
            .getJSONObject("response")
            .getJSONArray(arrayName)

        override fun parse(response: String): VKPostResponse {
            try {
                val items = getJSONArray(response, "items")
                val groups = getJSONArray(response, "groups")
                val nextFrom = JSONObject(response).getJSONObject("response").getString("next_from")
                val itemsList = ArrayList<VKPost>()
                val groupsMap = ConcurrentHashMap<Int, VKPostGroup>()
                for (i in 0 until groups.length()) {
                    val postGroup = VKPostGroup.parse(groups.getJSONObject(i))
                    groupsMap[postGroup.sourceID] = postGroup
                }
                for (i in 0 until items.length()) {
                    val post = VKPost.parse(items.getJSONObject(i))
                    val group = groupsMap[post.sourceID]
                    if (group != null) {
                        post.groupName = group.groupName
                        post.groupPhoto = group.groupPhoto
                        itemsList.add(post)
                    }
                }
                return VKPostResponse(itemsList, nextFrom)
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
