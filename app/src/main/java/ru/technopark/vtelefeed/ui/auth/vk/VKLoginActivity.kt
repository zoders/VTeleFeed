package ru.technopark.vtelefeed.ui.auth.vk

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException
import kotlinx.coroutines.MainScope
import ru.technopark.vtelefeed.R

/**
 * Created by Ilya Deydysh on 09.11.2021.
 */
class VKLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VK.login(this@VKLoginActivity, arrayListOf(VKScope.WALL, VKScope.PHOTOS, VKScope.FRIENDS))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                Toast.makeText(
                    this@VKLoginActivity,
                    "Success!",
                    Toast.LENGTH_LONG
                ).show()
//                MainActivity.startFrom(this@VKLoginActivity)
                finish()
            }

            override fun onLoginFailed(authException: VKAuthException) {
                Toast.makeText(
                    this@VKLoginActivity,
                    "Try again :(",
                    Toast.LENGTH_LONG
                ).show()
                val descriptionResource =
                    if (authException.webViewError == WebViewClient.ERROR_HOST_LOOKUP)
                        R.string.message_connection_error
                    else
                        R.string.message_unknown_error
                AlertDialog.Builder(this@VKLoginActivity)
                    .setMessage(descriptionResource)
                    .setPositiveButton(R.string.vk_retry) { _, _ ->
                        VK.login(
                            this@VKLoginActivity,
                            arrayListOf(VKScope.WALL, VKScope.PHOTOS, VKScope.FRIENDS)
                        )
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        fun startFrom(context: Context) {
            val intent = Intent(context, VKLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}
