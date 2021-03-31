package de.solarisbank.sdk.core.view

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

@Suppress("UNCHECKED_CAST")
@JvmName("viewBindingFragment")
public fun <F : Fragment, T : ViewBinding> Fragment.viewBinding(
        viewBinder: (F) -> T
): ViewBindingProperty<F, T> {
    return ViewBindingProperty(viewBinder)
}

@JvmName("viewBindingFragment")
public inline fun <F : Fragment, T : ViewBinding> Fragment.viewBinding(
        crossinline vbFactory: (LayoutInflater) -> T,
        crossinline viewProvider: (F) -> LayoutInflater = Fragment::getLayoutInflater
): ViewBindingProperty<F, T> {
    return viewBinding { fragment: F -> vbFactory(viewProvider(fragment)) }
}
