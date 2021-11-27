package ru.technopark.vtelefeed

import android.util.Log
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback

class VKDataSource {

    fun getVKResponse(startFrom: String = ""): VKPostResponse? {
        var response: VKPostResponse? = null
        VK.execute(
            VKNewsfeedCommand(startFrom = startFrom),
            object : VKApiCallback<VKPostResponse> {
                override fun success(result: VKPostResponse) { response = result }

                override fun fail(error: Exception) {
                    Log.e(TAG, error.toString())
                }
            }
        )
        return response
    }
    companion object {
        private const val TAG = "VKDataSource"
    }
}
