package ru.technopark.vtelefeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.drinkless.td.libcore.telegram.TdApi

class TelegramAuthorizationFragment : Fragment(), TelegramApp.Callback {
    private var telegramApp: TelegramApp? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filesDir = activity?.filesDir?.absoluteFile
        telegramApp = TelegramApp(this)
        telegramApp!!.appDir = filesDir.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_telegram_authorization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logTextView  = view.findViewById<TextView>(R.id.logTextView)
        Toast.makeText(activity, telegramApp!!.getTelegramAuthorizationState().toString(), Toast.LENGTH_SHORT).show()
        logTextView.text = telegramApp!!.getTelegramAuthorizationState().toString()
        view.findViewById<Button>(R.id.getCode).setOnClickListener{
            val phoneNumberView  = view.findViewById<EditText>(R.id.phoneNumber)
            telegramApp?.phoneNumber  = phoneNumberView.text.toString()
            telegramApp?.changeAuthorizationState()
        }
        view.findViewById<Button>(R.id.applyCode).setOnClickListener{
            val codeView  = view.findViewById<EditText>(R.id.code)
            telegramApp?.verificationCode = codeView.text.toString()
            telegramApp?.changeAuthorizationState()
        }
        view.findViewById<Button>(R.id.logOut).setOnClickListener{
            telegramApp?.logOut()

        }
    }

    override fun onResult(`object`: TdApi.Object) {
        when (`object`.constructor) {
            TdApi.UpdateAuthorizationState.CONSTRUCTOR -> telegramApp?.
            onAuthorizationStateUpdated((`object` as TdApi.UpdateAuthorizationState).authorizationState)
        }
    }

    companion object {
        fun newInstance(): TelegramAuthorizationFragment {
            return TelegramAuthorizationFragment()
        }
    }
}
