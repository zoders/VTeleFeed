package ru.technopark.vtelefeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.github.gongw.VerifyCodeView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class TelegramAuthorizationFragment : Fragment() {

    private var telegramClient: TelegramClient = TelegramClient.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = ("Ваш Telegram")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_telegram_authorization,
            container,
            false
        )
    }

    private fun setViewVisibility(view: View) {
        when (Companion.loginDialogType) {
            LoginDialogType.ENTER_PHONE_NUMBER -> {
                view.findViewById<View>(LoginDialogType.ENTER_PHONE_NUMBER.viewId)
                    ?.visibility = View.VISIBLE
                view.findViewById<View>(LoginDialogType.ENTER_CODE.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.ENTER_PASSWORD.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.LOGIN_READY.viewId)
                    ?.visibility = View.INVISIBLE
            }
            LoginDialogType.ENTER_CODE -> {
                view.findViewById<View>(LoginDialogType.ENTER_PHONE_NUMBER.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.ENTER_CODE.viewId)
                    ?.visibility = View.VISIBLE
                view.findViewById<View>(LoginDialogType.ENTER_PASSWORD.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.LOGIN_READY.viewId)
                    ?.visibility = View.INVISIBLE
            }
            LoginDialogType.ENTER_PASSWORD -> {
                view.findViewById<View>(LoginDialogType.ENTER_PHONE_NUMBER.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.ENTER_CODE.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.ENTER_PASSWORD.viewId)
                    ?.visibility = View.VISIBLE
                view.findViewById<View>(LoginDialogType.LOGIN_READY.viewId)
                    ?.visibility = View.INVISIBLE
            }
            LoginDialogType.LOGIN_READY -> {
                view.findViewById<View>(LoginDialogType.ENTER_PHONE_NUMBER.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.ENTER_CODE.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.ENTER_PASSWORD.viewId)
                    ?.visibility = View.INVISIBLE
                view.findViewById<View>(LoginDialogType.LOGIN_READY.viewId)
                    ?.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViewVisibility(view)
        var verificationData: String?
        super.onViewCreated(view, savedInstanceState)
        when (loginDialogType) {
            LoginDialogType.ENTER_PHONE_NUMBER -> {
                val phoneNumberView = view.findViewById<EditText>(R.id.phoneNumber)
                val formatWatcher: FormatWatcher = MaskFormatWatcher(
                    MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
                )
                formatWatcher.installOn(phoneNumberView)
                view.findViewById<FloatingActionButton>(R.id.getCodeFab)?.setOnClickListener {
                    verificationData = phoneNumberView?.text?.replace(
                        "\\s|\\(|\\)|-".toRegex(),
                        ""
                    )
                    if (verificationData != null && loginDialogType != null) {
                        getMainActivity()?.applyAuthParam(loginDialogType!!, verificationData!!)
                    }
                }
            }
            LoginDialogType.ENTER_CODE -> {
                view.findViewById<VerifyCodeView>(R.id.verCode).setOnAllFilledListener {
                    val verCodeView = view.findViewById<VerifyCodeView>(R.id.verCode)
                    verificationData = verCodeView.vcText.toString()
                    if (verificationData != null && loginDialogType != null) {
                        getMainActivity()?.applyAuthParam(loginDialogType!!, verificationData!!)
                    }
                }
            }
            LoginDialogType.ENTER_PASSWORD -> {
                val passwordView = view.findViewById<EditText>(R.id.password)
                view.findViewById<FloatingActionButton>(R.id.sendPasswordFab)?.setOnClickListener {
                    verificationData = passwordView.text.toString()
                    if (verificationData != null && loginDialogType != null) {
                        getMainActivity()?.applyAuthParam(loginDialogType!!, verificationData!!)
                    }
                }
            }
            LoginDialogType.LOGIN_READY -> {
                val profilePic = view.findViewById<ImageView>(R.id.profilePic)
                Glide.with(requireActivity())
                    .load(telegramClient.getUserPhotoPath(telegramClient.user))
                    .error(R.drawable.default_userpic)
                    .into(profilePic)
                val welcomeTextView = view.findViewById<TextView>(R.id.welcome)
                if (telegramClient.user != null) {
                    welcomeTextView?.text =
                        "Добро пожаловать, " + telegramClient.user?.username + '!'
                } else {
                    welcomeTextView?.text =
                        "Добро пожаловать, " + telegramClient.user!!.username.toString() + '!'
                }
            }
        }
    }

    private fun getMainActivity(): MainActivity? {
        val activity = this.activity
        return if (activity != null) {
            activity as MainActivity
        } else {
            null
        }
    }

    companion object {
        private var fragment: TelegramAuthorizationFragment? = null
        private const val TAG = "TelegramAuthorizationFragment"
        var loginDialogType: LoginDialogType? = null
        var rootViewId: Int? = null

        enum class LoginDialogType(val viewId: Int) {
            ENTER_PHONE_NUMBER(R.id.enter_phone_number_layout),
            ENTER_CODE(R.id.enter_code_layout),
            ENTER_PASSWORD(R.id.password_layout),
            LOGIN_READY(R.id.ready_layout)
        }

        private fun newInstance(): TelegramAuthorizationFragment {
            return TelegramAuthorizationFragment()
        }
        fun showDialog(
            supportFragmentManager: FragmentManager,
            loginDialogType: LoginDialogType
        ) {
            if (fragment == null) {
                fragment = newInstance()
                if (rootViewId != null) {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            rootViewId!!,
                            fragment!!,
                            TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                val fragment = supportFragmentManager.findFragmentByTag(TAG)!!
                supportFragmentManager.beginTransaction()
                    .detach(fragment)
                    .commit()
                supportFragmentManager.beginTransaction()
                    .attach(fragment)
                    .commit()
            }
            this.loginDialogType = loginDialogType
        }
    }
}
