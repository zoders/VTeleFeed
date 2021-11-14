package ru.technopark.vtelefeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ru.technopark.vtelefeed.databinding.ActivityMainBinding
import ru.technopark.vtelefeed.utils.viewBinding

class AuthFragment : Fragment(R.layout.activity_main) {

    private val binding: ActivityMainBinding by viewBinding { ActivityMainBinding.bind(requireView()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
