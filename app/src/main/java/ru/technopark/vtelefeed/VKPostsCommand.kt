package ru.technopark.vtelefeed

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Ilya Deydysh on 11.11.2021.
 */
const val DEFAULT_COUNT = 50
const val DEFAULT_START_TIME = 1L

class VKPostsCommand(
        private val startTime: Long = DEFAULT_START_TIME, //unixtime
        private val endTime: Long = System.currentTimeMillis(), //unixtime
        private val count: Int = DEFAULT_COUNT // кол-во записей, но не более 100
    ): ApiCommand<List<VKPost>>() {
    override fun onExecute(manager: VKApiManager): List<VKPost> {
        val result = ArrayList<VKPost>()
        val call = VKMethodCall.Builder()
            .method("newsfeed.get")
            .args("filters", "post")
            .args("start_time", startTime)
            .args("end_time", endTime)
            .args("count", count)
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }

    private class ResponseApiParser : VKApiResponseParser<List<VKPost>> {
        override fun parse(response: String): List<VKPost> {
            try {
                val ja = JSONObject(response).getJSONObject("response").getJSONArray("items")
                val r = ArrayList<VKPost>(ja.length())
                for (i in 0 until ja.length()) {
                    val post = VKPost.parse(ja.getJSONObject(i))
                    r.add(post)
                }
                return r
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}