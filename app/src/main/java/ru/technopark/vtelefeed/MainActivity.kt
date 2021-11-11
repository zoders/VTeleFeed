package ru.technopark.vtelefeed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.technopark.vtelefeed.databinding.ActivityMainBinding

/**
 * Created by Ilya Deydysh on 09.11.2021.
 */
class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)

        if (VK.isLoggedIn()) {
            requestVKPosts() //обращение к серверу из ui потока?
            val user: VKUser
            mainActivityBinding.imbuttonVkLogout.visibility = View.VISIBLE
            mainActivityBinding.buttonNameVk.visibility = View.VISIBLE
            mainActivityBinding.buttonLoginVk.visibility = View.GONE
            requestVKUser()
        } else {
            mainActivityBinding.imbuttonVkLogout.visibility = View.INVISIBLE
            mainActivityBinding.buttonNameVk.visibility = View.GONE
            mainActivityBinding.buttonLoginVk.visibility = View.VISIBLE
        }

        setContentView(mainActivityBinding.root)

        mainActivityBinding.buttonLoginVk.setOnClickListener {
            VKLoginActivity.startFrom(this@MainActivity)
        }

        mainActivityBinding.imbuttonVkLogout.setOnClickListener {
            VK.logout()
            startFrom(this)
            finish()
        }
    }

    companion object {
        fun startFrom(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private fun requestVKUser() {
        VK.execute(
            VKUserCommand(),
            object : VKApiCallback<VKUser> {
                override fun success(result: VKUser) {
                    val vkNameButton = findViewById<TextView>(R.id.button_name_vk)
                    vkNameButton.text = "${result.firstName} ${result.lastName}"
                    val vkAvatarIV = findViewById<ImageView>(R.id.image_vk_profile_pic)

                    if (!TextUtils.isEmpty(result.photo)) {
                        Glide.with(this@MainActivity)
                            .load(result.photo)
                            .error(R.drawable.user_placeholder)
                            .into(vkAvatarIV)
                    } else {
                        vkAvatarIV.setImageResource(R.drawable.user_placeholder)
                    }
                }

                override fun fail(error: Exception) {
                    Log.e(tag, error.toString())
                }
            }
        )
    }

    private fun requestVKPosts(){
        VK.execute(
            VKPostsCommand(),
            object : VKApiCallback<List<VKPost>> {
                override fun success(result: List<VKPost>) {
                    if (!isFinishing && !result.isEmpty()) {
                        // обработка результата
                    }
                }

                override fun fail(error: Exception) {
                    Log.e(tag, error.toString())
                }
            }
        )
    }
}
