package ru.technopark.vtelefeed

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler

class VTeleFeedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
//        TelegramClient.instance.createClient(this)
    }

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            MainActivity.startFrom(this@VTeleFeedApplication)
        }
    }
}
