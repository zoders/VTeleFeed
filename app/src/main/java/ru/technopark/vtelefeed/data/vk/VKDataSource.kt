package ru.technopark.vtelefeed.data.vk

import android.util.Log
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.technopark.vtelefeed.data.VKPost
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VKDataSource {
    var nextFrom: String = ""
    suspend fun getVKResponse(startFrom: String = ""): List<VKPost> {
        val news: MutableList<VKPost> = mutableListOf()
        var response: VKPostResponse?
        return suspendCoroutine { cont ->
            val callback = object : VKApiCallback<VKPostResponse> {
                override fun fail(error: Exception) {
                    Log.e(TAG, error.toString())
                    cont.resume(news)
                }

                override fun success(result: VKPostResponse) {
                    nextFrom = result.nextFrom
                    response = result
                    if (response != null) news.addAll(response!!.vkPosts)
                    cont.resume(news)
                }
            }
            VK.execute(
                VKNewsfeedCommand(startFrom = startFrom),
                callback
            )
        }
    }

    companion object {
        private const val TAG = "VKDataSource"
    }
}
