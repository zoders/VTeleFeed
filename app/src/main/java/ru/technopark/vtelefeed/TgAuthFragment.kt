package ru.technopark.vtelefeed

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.databinding.FragmentTgAuthBinding
import ru.technopark.vtelefeed.utils.Loading
import ru.technopark.vtelefeed.utils.snackBar
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

        savedInstanceState?.let { restoreViewState(it) }

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneNumberEditText)

        binding.progressBar.isVisible

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
                binding.doneButton.isVisible = !isReady
                binding.progressBar.isVisible = !isReady
                val a = 5
            }
        }

        viewModel.userPhoto.observe(viewLifecycleOwner) { photo ->
            Glide.with(this)
                .load(photo.local.path)
                .into(binding.userPhoto)
        }

        binding.doneButton.setOnClickListener {
            viewModel.authState.value?.let {
                when (it) {
                    is TdApi.AuthorizationStateWaitPhoneNumber -> setPhoneNumber()
                    is TdApi.AuthorizationStateWaitCode -> setAuthCode()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(DONE_FAB_VISIBILITY, binding.doneButton.isVisible)
        outState.putBoolean(PROGRESSBAR_VISIBILITY, binding.doneButton.isVisible)
    }

    private fun restoreViewState(savedInstanceState: Bundle) {
        binding.doneButton.isVisible =
            savedInstanceState.getBoolean(DONE_FAB_VISIBILITY, true)
        binding.progressBar.isVisible =
            savedInstanceState.getBoolean(PROGRESSBAR_VISIBILITY, false)
    }

    private fun showLoading(show: Boolean) {
        binding.doneButton.isVisible = !show
        binding.progressBar.isVisible = show
    }

    private fun setPhoneNumber() {
        viewModel.setPhoneNumber(binding.phoneNumberEditText.text.toString())
            .observe(viewLifecycleOwner) { obj ->
                showLoading(obj is Loading)
                when (obj) {
                    is TdApi.Ok -> snackBar("Phone number success")
                    is TdApi.Error -> snackBar(obj.message)
                }
            }
    }

    private fun setAuthCode() {
        viewModel.setWaitCode(binding.verificationView.vcText)
            .observe(viewLifecycleOwner) { obj ->
                showLoading(obj is Loading)
                when (obj) {
                    is TdApi.Ok -> {
                        snackBar("Confirmation code success")
                        binding.progressBar.isVisible = false
                        binding.progressBar.isVisible = false
                        val a = 5
                    }
                    is TdApi.Error -> snackBar(obj.message)
                }
            }
    }

    companion object {
        private const val DONE_FAB_VISIBILITY = "done fab visibility"
        private const val PROGRESSBAR_VISIBILITY = "progressbar visibility"
    }
}
