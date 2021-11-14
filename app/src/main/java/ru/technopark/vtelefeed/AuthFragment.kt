package ru.technopark.vtelefeed

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.technopark.vtelefeed.databinding.FragmentAuthBinding
import ru.technopark.vtelefeed.utils.viewBinding

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding: FragmentAuthBinding by viewBinding {
        FragmentAuthBinding.bind(requireView())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            updateVkLoginUI()

            buttonLoginVk.setOnClickListener {
                VKLoginActivity.startFrom(requireContext())
            }

            imbuttonVkLogout.setOnClickListener {
                VK.logout()
                updateVkLoginUI()
            }

            if (VK.isLoggedIn()) {
                requestVKUser()
            }
        }
    }

    private fun updateVkLoginUI() {
        with(binding) {
            val isVkLogged = VK.isLoggedIn()
            imbuttonVkLogout.isVisible = isVkLogged
            buttonNameVk.isVisible = isVkLogged
            buttonLoginVk.isVisible = !isVkLogged
        }
    }

    private fun requestVKUser() {
        VK.execute(
            VKUserCommand(),
            object : VKApiCallback<VKUser> {
                override fun success(result: VKUser) {

                    binding.buttonNameVk.text = "${result.firstName} ${result.lastName}"

                    if (!TextUtils.isEmpty(result.photo)) {
                        Glide.with(this@AuthFragment)
                            .load(result.photo)
                            .error(R.drawable.user_placeholder)
                            .into(binding.imageVkProfilePic)
                    } else {
                        binding.imageVkProfilePic.setImageResource(R.drawable.user_placeholder)
                    }
                }

                override fun fail(error: Exception) {
                    Log.e(tag, error.toString())
                }
            }
        )
    }

    companion object {
        fun newInstance() = AuthFragment()
    }
}
