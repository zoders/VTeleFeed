package ru.technopark.vtelefeed

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.data.db.PostsDatabase
import ru.technopark.vtelefeed.data.tg.TgClient
import ru.technopark.vtelefeed.ui.MainActivity

class VTeleFeedApplication : Application() {

    val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()

        instance = this

        // Triggers client creation
        applicationScope.launch {
            TgClient.clientFlow.collect { }
        }

        applicationScope.launch {
            TgClient.authStateFlow.collect { state ->
                if (state is TdApi.AuthorizationStateReady) {
                    TgClient.initMyUser()
                }
            }
        }

        PostsDatabase.instance

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
