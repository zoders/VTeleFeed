package ru.technopark.vtelefeed.ui

import androidx.fragment.app.Fragment

interface FragmentInteractor {
    fun back()
    fun openFragment(fragment: Fragment)
}
