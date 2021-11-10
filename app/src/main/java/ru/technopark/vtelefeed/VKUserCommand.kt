package ru.technopark.vtelefeed

import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Ilya Deydysh on 09.11.2021.
 */
class VKUserCommand : ApiCommand<VKUser>() {
    override fun onExecute(manager: VKApiManager): VKUser {
        val call = VKMethodCall.Builder()
            .method("users.get")
            .args("fields", "photo_50")
            .version(manager.config.version)
            .build()
        return manager.execute(call, ResponseApiParser())
    }

    private class ResponseApiParser : VKApiResponseParser<VKUser> {
        override fun parse(response: String): VKUser {
            try {
                val json = JSONObject(response).getJSONArray("response")
                return VKUser.parse(json.getJSONObject(0))
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}
