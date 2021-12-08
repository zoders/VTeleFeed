package ru.technopark.vtelefeed.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.vk.api.sdk.utils.VKUtils
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.databinding.ActivityMainBinding
import ru.technopark.vtelefeed.ui.postlist.PostListFragment

class MainActivity : AppCompatActivity(), FragmentInteractor {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val fingerprints = VKUtils.getCertificateFingerprint(this, packageName)
//        Log.i(TAG, fingerprints?.get(0).orEmpty())
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
