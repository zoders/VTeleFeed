package ru.technopark.vtelefeed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.technopark.vtelefeed.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainActivityBinding.root)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = PostListFragment.newInstance()
            supportFragmentManager.beginTransaction().add(
                R.id.fragment_container,
                fragment
            ).commit()

            supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, TgAuthFragment())
                .commit()
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        fun startFrom(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}
