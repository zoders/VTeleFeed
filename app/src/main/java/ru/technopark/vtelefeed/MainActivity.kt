package ru.technopark.vtelefeed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.technopark.vtelefeed.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainActivityBinding.root)

    }

}
