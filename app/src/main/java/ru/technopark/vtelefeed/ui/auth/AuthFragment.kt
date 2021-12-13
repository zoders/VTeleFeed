package ru.technopark.vtelefeed.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.vk.VKUser
import ru.technopark.vtelefeed.data.vk.VKUserCommand
import ru.technopark.vtelefeed.databinding.FragmentAuthBinding
import ru.technopark.vtelefeed.ui.FragmentInteractor
import ru.technopark.vtelefeed.ui.auth.tg.TgAuthFragment
import ru.technopark.vtelefeed.ui.auth.vk.VKLoginActivity
import ru.technopark.vtelefeed.utils.viewBinding

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding: FragmentAuthBinding by viewBinding {
        FragmentAuthBinding.bind(requireView())
    }

    private var fragmentInteractor: FragmentInteractor? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentInteractor = activity as FragmentInteractor
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            updateVkLoginUI()

            authToolbar.setNavigationOnClickListener {
                fragmentInteractor?.back()
            }

            buttonLoginVk.setOnClickListener {
                VKLoginActivity.startFrom(requireContext())
            }

            imbuttonVkLogout.setOnClickListener {
                VK.logout()
                updateVkLoginUI()
            }
            buttonLoginTelegram.setOnClickListener {
                fragmentInteractor?.openFragment(TgAuthFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateVkLoginUI()
    }

    private fun updateVkLoginUI() {
        with(binding) {
            val isVkLogged = VK.isLoggedIn()
            if (isVkLogged) {
                requestVKUser()
                imageVkProfilePic.visibility = VISIBLE
                imbuttonVkLogout.visibility = VISIBLE
                buttonNameVk.visibility = VISIBLE
                buttonLoginVk.visibility = INVISIBLE
            } else {
                imageVkProfilePic.visibility = INVISIBLE
                imbuttonVkLogout.visibility = INVISIBLE
                buttonNameVk.visibility = INVISIBLE
                buttonLoginVk.visibility = VISIBLE
            }
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
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
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
