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
import ru.technopark.vtelefeed.data.tg.TgClient
import ru.technopark.vtelefeed.ui.MainActivity
import java.util.concurrent.CancellationException

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

        VK.addTokenExpiredHandler(tokenTracker)
    }

    override fun onTerminate() {
        applicationScope.cancel(CancellationException("Application terminated"))
        super.onTerminate()
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
