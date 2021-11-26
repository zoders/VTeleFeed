package ru.technopark.vtelefeed

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.drinkless.td.libcore.telegram.TdApi

class VTeleFeedApplication : Application() {

    val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()

        instance = this

        // Triggers client creation
        applicationScope.launch {
            TgClient.clientFlow.collect {
                val a = 5
            }
        }

        applicationScope.launch {
            TgClient.authStateFlow.collect { state ->
                if (state is TdApi.AuthorizationStateReady) {
                    TgClient.initMyUser()
                }
            }
        }

        VK.addTokenExpiredHandler(tokenTracker)
//        TelegramClient.instance.createClient(this)
    }

    override fun onTerminate() {
        super.onTerminate()

        applicationScope.cancel()
    }

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            MainActivity.startFrom(this@VTeleFeedApplication)
        }
    }

    companion object {
        lateinit var instance: VTeleFeedApplication
    }
}
