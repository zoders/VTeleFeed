package ru.technopark.vtelefeed.utils

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(requireView(), message, duration).show()
}
