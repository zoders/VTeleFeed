package ru.technopark.vtelefeed

import android.os.Bundle
import android.util.Log
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
    private var telegramAuthorizationRequestHandler: TelegramClient.TelegramAuthorizationRequestHandler? = null
    private var telegramClient: TelegramClient = TelegramClient.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        telegramClient.appDir = activity?.filesDir.toString()
        telegramClient.createClient()
        telegramAuthorizationRequestHandler = telegramClient.setTelegramAuthorizationRequestHandler(
            object : TelegramClient.TelegramAuthorizationRequestListener {
                override fun onRequestTelegramAuthenticationParameter(
                    telegramAuthenticationParameterType: TelegramClient.TelegramAuthenticationParameterType
                ) {
                    showTelegramLogin(telegramAuthenticationParameterType)
                }

                override fun onTelegramAuthorizationRequestError(code: Int, message: String) {
                    Toast.makeText(activity, "$code - $message", Toast.LENGTH_LONG)
                        .show()

                    Log.e("AuthorizationRequestError", "$code - $message")
                }
            }
        )
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

    private fun showTelegramLogin(
        telegramAuthenticationParameterType: TelegramClient.TelegramAuthenticationParameterType
    ) {

        when (telegramAuthenticationParameterType) {
            TelegramClient.TelegramAuthenticationParameterType.PHONE_NUMBER -> {
                showFragment(
                    Companion.LoginType.ENTER_PHONE_NUMBER,
                    activity?.supportFragmentManager!!
                )
                loginType = Companion.LoginType.ENTER_PHONE_NUMBER
            }
            TelegramClient.TelegramAuthenticationParameterType.CODE -> {
                showFragment(
                    Companion.LoginType.ENTER_CODE,
                    activity?.supportFragmentManager!!
                )
                loginType = Companion.LoginType.ENTER_CODE
            }
            TelegramClient.TelegramAuthenticationParameterType.PASSWORD -> {
                showFragment(
                    Companion.LoginType.ENTER_PASSWORD,
                    activity?.supportFragmentManager!!
                )
                loginType = Companion.LoginType.ENTER_PASSWORD
            }
            TelegramClient.TelegramAuthenticationParameterType.READY -> {
                showFragment(
                    Companion.LoginType.LOGIN_READY,
                    activity?.supportFragmentManager!!
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViewVisibility(view)
        var verificationData: String?
        when (loginType) {
            LoginType.ENTER_PHONE_NUMBER -> {
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
                    if (verificationData != null && loginType != null) {
                        telegramClient.applyAuthParam(loginType!!, verificationData!!)
                    }
                }
            }
            LoginType.ENTER_CODE -> {
                view.findViewById<VerifyCodeView>(R.id.verCode).setOnAllFilledListener {
                    val verCodeView = view.findViewById<VerifyCodeView>(R.id.verCode)
                    verificationData = verCodeView.vcText.toString()
                    if (verificationData != null && loginType != null) {
                        telegramClient.applyAuthParam(loginType!!, verificationData!!)
                    }
                }
            }
            LoginType.ENTER_PASSWORD -> {
                val passwordView = view.findViewById<EditText>(R.id.password)
                view.findViewById<FloatingActionButton>(R.id.sendPasswordFab)?.setOnClickListener {
                    verificationData = passwordView.text.toString()
                    if (verificationData != null && loginType != null) {
                        telegramClient.applyAuthParam(loginType!!, verificationData!!)
                    }
                }
            }
            LoginType.LOGIN_READY -> {
                val profilePic = view.findViewById<ImageView>(R.id.profilePic)
                Glide.with(requireActivity())
                    .load(telegramClient.getUserPhotoPath(telegramClient.user))
                    .error(R.drawable.user_placeholder)
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

    companion object {
        private var fragment: TelegramAuthorizationFragment? = null
        private const val TAG = "TelegramAuthorizationFragment"
        var loginType: LoginType? = null
        var rootViewId: Int = R.id.fragment_container

        enum class LoginType(val viewId: Int) {
            ENTER_PHONE_NUMBER(R.id.enter_phone_number_layout),
            ENTER_CODE(R.id.enter_code_layout),
            ENTER_PASSWORD(R.id.password_layout),
            LOGIN_READY(R.id.ready_layout)
        }

        private fun newInstance(): TelegramAuthorizationFragment {
            return TelegramAuthorizationFragment()
        }

        private fun setViewVisibility(view: View) {
            when (loginType) {
                LoginType.ENTER_PHONE_NUMBER -> {
                    view.findViewById<View>(LoginType.ENTER_PHONE_NUMBER.viewId)
                        ?.visibility = View.VISIBLE
                    view.findViewById<View>(LoginType.ENTER_CODE.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.ENTER_PASSWORD.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.LOGIN_READY.viewId)
                        ?.visibility = View.INVISIBLE
                }
                LoginType.ENTER_CODE -> {
                    view.findViewById<View>(LoginType.ENTER_PHONE_NUMBER.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.ENTER_CODE.viewId)
                        ?.visibility = View.VISIBLE
                    view.findViewById<View>(LoginType.ENTER_PASSWORD.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.LOGIN_READY.viewId)
                        ?.visibility = View.INVISIBLE
                }
                LoginType.ENTER_PASSWORD -> {
                    view.findViewById<View>(LoginType.ENTER_PHONE_NUMBER.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.ENTER_CODE.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.ENTER_PASSWORD.viewId)
                        ?.visibility = View.VISIBLE
                    view.findViewById<View>(LoginType.LOGIN_READY.viewId)
                        ?.visibility = View.INVISIBLE
                }
                LoginType.LOGIN_READY -> {
                    view.findViewById<View>(LoginType.ENTER_PHONE_NUMBER.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.ENTER_CODE.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.ENTER_PASSWORD.viewId)
                        ?.visibility = View.INVISIBLE
                    view.findViewById<View>(LoginType.LOGIN_READY.viewId)
                        ?.visibility = View.VISIBLE
                }
            }
        }

        fun showFragment(
            loginType: LoginType,
            supportFragmentManager: FragmentManager
        ) {
            if (fragment == null) {
                /*val rootFr = supportFragmentManager.findFragmentByTag("TAG")
                val id = rootFr?.id
                fragment = newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(
                        id!!,
                        fragment!!,
                        TAG
                    )
                    .addToBackStack(null)
                    .commit()*/
                fragment = newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(
                        rootViewId,
                        fragment!!,
                        TAG
                    )
                    .addToBackStack(null)
                    .commit()
            } else {
                val fragment = supportFragmentManager.findFragmentByTag(TAG)!!
                supportFragmentManager.beginTransaction()
                    .detach(fragment)
                    .commit()
                supportFragmentManager.beginTransaction()
                    .attach(fragment)
                    .commit()
            }
            this.loginType = loginType
        }
    }
}
