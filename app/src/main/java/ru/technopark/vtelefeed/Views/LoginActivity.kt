package ru.technopark.vtelefeed.Views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException
import ru.technopark.vtelefeed.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginActivityBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginActivityBinding.root)

        loginActivityBinding.buttonLoginVk.setOnClickListener {
            VK.login(this@LoginActivity, arrayListOf(VKScope.WALL))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                Toast.makeText(this@LoginActivity, "Success!", Toast.LENGTH_LONG).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                // User passed authorization
            }

            override fun onLoginFailed(authException: VKAuthException) {
                Toast.makeText(this@LoginActivity, "Try again :(", Toast.LENGTH_LONG).show()
                TODO("Not yet implemented")
                // User didn't pass authorization
            }

        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}