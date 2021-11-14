@file:Suppress("unused")

package ru.technopark.vtelefeed.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewBindingFragmentDelegate<VB : ViewBinding>(
    private val inflateBinding: () -> VB
) :
    ReadOnlyProperty<Fragment, VB>,
    LifecycleEventObserver {

    private var binding: VB? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        thisRef.viewLifecycleOwner.lifecycle.addObserver(this)

        binding = binding ?: inflateBinding()
        return binding!!
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            clearBinding()
        }
    }

    private fun clearBinding() {
        binding = null
    }
}

fun <VB : ViewBinding> Fragment.viewBinding(inflateBinding: () -> VB): ViewBindingFragmentDelegate<VB> {
    return ViewBindingFragmentDelegate(inflateBinding)
}
