package ru.technopark.vtelefeed

import androidx.fragment.app.Fragment

interface FragmentInteractor {
    fun back()
    fun openFragment(fragment: Fragment)
}
