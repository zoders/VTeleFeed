package ru.technopark.vtelefeed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.technopark.vtelefeed.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FragmentInteractor {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(mainActivityBinding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment_container, PostListFragment.newInstance())
            }
        }
    }

    override fun back() {
        supportFragmentManager.popBackStack()
    }

    override fun openFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        fun startFrom(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}
