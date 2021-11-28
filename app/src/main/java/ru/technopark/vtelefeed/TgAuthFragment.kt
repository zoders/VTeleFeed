package ru.technopark.vtelefeed

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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

    private var fragmentInteractor: FragmentInteractor? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentInteractor = activity as FragmentInteractor
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneNumberEditText)

        binding.tgAuthToolbar.setNavigationOnClickListener {
            fragmentInteractor?.back()
        }

        viewModel.authState.observe(viewLifecycleOwner) {
            it?.let { authState ->
                val isWaitPhoneNumber = authState is TdApi.AuthorizationStateWaitPhoneNumber
                binding.phoneNumberEditText.isVisible = isWaitPhoneNumber
                binding.phoneIcon.isVisible = isWaitPhoneNumber

                val isWaitCode = authState is TdApi.AuthorizationStateWaitCode
                binding.verificationView.isVisible = isWaitCode
                binding.verificationIcon.isVisible = isWaitCode

                val isWaitPassword = authState is TdApi.AuthorizationStateWaitPassword
                binding.passwordEditText.isVisible = isWaitPassword
                binding.passwordIcon.isVisible = isWaitPassword

                val isReady = authState is TdApi.AuthorizationStateReady
                binding.userPhoto.isVisible = isReady
                if (isReady) {
                    binding.doneButton.isVisible = false
                    binding.progressBar.isVisible = false
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.snackBars.collect { message ->
                    snackBar(message)
                }
            }
        }

        observePhoneNumber()
        observeAuthCode()
        observePassword()

        viewModel.userPhoto.observe(viewLifecycleOwner) { photo ->
            Glide.with(this)
                .load(photo.local.path)
                .into(binding.userPhoto)
        }

        binding.doneButton.setOnClickListener {
            viewModel.authState.value?.let {
                when (it) {
                    is TdApi.AuthorizationStateWaitPhoneNumber ->
                        viewModel.setPhoneNumber(binding.phoneNumberEditText.text.toString())
                    is TdApi.AuthorizationStateWaitCode ->
                        viewModel.setWaitCode(binding.verificationView.vcText)
                    is TdApi.AuthorizationStateWaitPassword ->
                        viewModel.setPassword(binding.passwordEditText.text.toString())
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.doneButton.isVisible = !show
        binding.progressBar.isVisible = show
    }

    private fun observePhoneNumber() {
        viewModel.phoneNumber.observe(viewLifecycleOwner) { obj ->
            showLoading(obj is Loading)
            when (obj) {
                is TdApi.Ok ->
                    viewModel.onSnackBar(
                        requireContext().getString(R.string.code_has_been_sent)
                    )
                is TdApi.Error -> viewModel.onSnackBar(obj.message)
            }
        }
    }

    private fun observeAuthCode() {
        viewModel.authCode.observe(viewLifecycleOwner) { obj ->
            showLoading(obj is Loading)
            when (obj) {
                is TdApi.Ok ->
                    viewModel.onSnackBar(requireContext().getString(R.string.auth_code_success))
                is TdApi.Error -> viewModel.onSnackBar(obj.message)
            }
        }
    }

    private fun observePassword() {
        viewModel.password.observe(viewLifecycleOwner) { obj ->
            showLoading(obj is Loading)
            when (obj) {
                is TdApi.Ok ->
                    viewModel.onSnackBar(requireContext().getString(R.string.password_success))
                is TdApi.Error -> {
                    viewModel.onSnackBar(obj.message)
                }
            }
        }
    }
}
