package ru.technopark.vtelefeed

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private val telegramClient: TelegramClient = TelegramClient.instance
    var telegramAuthorizationRequestHandler: TelegramClient.TelegramAuthorizationRequestHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val filesDir = filesDir.absoluteFile
        telegramClient.appDir = filesDir.toString()
        telegramClient.createClient()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, Fragment()).addToBackStack(null).commit()
        TelegramAuthorizationFragment.rootViewId = R.id.fragment_container
        telegramAuthorizationRequestHandler = telegramClient.setTelegramAuthorizationRequestHandler(
            object : TelegramClient.TelegramAuthorizationRequestListener {
                override fun onRequestTelegramAuthenticationParameter(
                    telegramAuthenticationParameterType: TelegramClient.TelegramAuthenticationParameterType
                ) {
                    runOnUiThread {
                        showTelegramLogin(telegramAuthenticationParameterType)
                    }
                }

                override fun onTelegramAuthorizationRequestError(code: Int, message: String) {
                    Log.e("AuthorizationRequestError", "$code - $message")
                }
            }
        )
    }

    private fun showTelegramLogin(
        telegramAuthenticationParameterType: TelegramClient.TelegramAuthenticationParameterType
    ) {
        when (telegramAuthenticationParameterType) {
            TelegramClient.TelegramAuthenticationParameterType.PHONE_NUMBER -> {
                TelegramAuthorizationFragment.showDialog(
                    supportFragmentManager,
                    TelegramAuthorizationFragment.Companion.LoginDialogType.ENTER_PHONE_NUMBER
                )
            }
            TelegramClient.TelegramAuthenticationParameterType.CODE -> {
                TelegramAuthorizationFragment.showDialog(
                    supportFragmentManager,
                    TelegramAuthorizationFragment.Companion.LoginDialogType.ENTER_CODE
                )
            }
            TelegramClient.TelegramAuthenticationParameterType.READY -> {
                TelegramAuthorizationFragment.showDialog(
                    supportFragmentManager,
                    TelegramAuthorizationFragment.Companion.LoginDialogType.LOGIN_READY
                )
            }
        }
    }

    fun applyAuthParam(
        loginDialogType: TelegramAuthorizationFragment.Companion.LoginDialogType,
        text: String
    ) {
        when (loginDialogType) {
            TelegramAuthorizationFragment.Companion.LoginDialogType.ENTER_PHONE_NUMBER -> {
                telegramAuthorizationRequestHandler
                    ?.applyAuthenticationParameter(
                        TelegramClient
                            .TelegramAuthenticationParameterType
                            .PHONE_NUMBER,
                        text
                    )
            }
            TelegramAuthorizationFragment.Companion.LoginDialogType.ENTER_CODE -> {
                telegramAuthorizationRequestHandler
                    ?.applyAuthenticationParameter(
                        TelegramClient
                            .TelegramAuthenticationParameterType
                            .CODE,
                        text
                    )
            }
        }
    }
}
