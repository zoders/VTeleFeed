package ru.technopark.vtelefeed

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.databinding.FragmentTgAuthBinding
import ru.technopark.vtelefeed.utils.viewBinding
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class TgAuthFragment : Fragment(R.layout.fragment_tg_auth) {

    private val binding: FragmentTgAuthBinding by viewBinding {
        FragmentTgAuthBinding.bind(requireView())
    }

    private val viewModel: TgAuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneNumberEditText)

        viewModel.authState.observe(viewLifecycleOwner) {
            it?.let { authState ->

                val isWaitPhoneNumber = authState is TdApi.AuthorizationStateWaitPhoneNumber
                binding.phoneNumberEditText.isVisible = isWaitPhoneNumber
                binding.phoneIcon.isVisible = isWaitPhoneNumber

                val isWaitCode = authState is TdApi.AuthorizationStateWaitCode
                binding.verificationView.isVisible = isWaitCode
                binding.verificationIcon.isVisible = isWaitCode

                val isReady = authState is TdApi.AuthorizationStateReady
                binding.userPhoto.isVisible = isReady
                if (isReady) {
                    viewModel.loadUserPhoto()
                }
            }
        }

        viewModel.userPhoto.observe(viewLifecycleOwner) { photo ->
            Glide.with(this)
                .load(photo.minithumbnail?.data)
                .into(binding.userPhoto)
        }

        binding.doneButton.setOnClickListener {
            viewModel.authState.value?.let {
                when (it) {
                    is TdApi.AuthorizationStateWaitPhoneNumber ->
                        viewModel.setPhoneNumber(binding.phoneNumberEditText.text.toString())
                    is TdApi.AuthorizationStateWaitCode ->
                        viewModel.setWaitCode(binding.verificationView.vcText)
                }
            }
        }
    }
}