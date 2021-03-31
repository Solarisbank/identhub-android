package de.solarisbank.sdk.core.view

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ViewBindingProperty<F : Fragment, VB : ViewBinding>(
        private val viewBinder: (F) -> VB
) : ReadOnlyProperty<F, VB>, LifecycleObserver {
    private var viewBinding: VB? = null

    override fun getValue(thisRef: F, property: KProperty<*>): VB {
        if (viewBinding != null) {
            return viewBinding!!
        }

        val lifecycle = thisRef.lifecycle
        val viewBinding = viewBinder(thisRef)
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            Log.w("ViewBindingProperty", "Access to viewBinding after Lifecycle is destroyed or hasn't created yet. " +
                    "The instance of viewBinding will be not cached."
            )
            // We can access to ViewBinding after Fragment.onDestroyView(), but don't save it to prevent memory leak
        } else {
            lifecycle.addObserver(this)
            this.viewBinding = viewBinding
        }
        return viewBinding
    }

    @MainThread
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        mainHandler.post {
            viewBinding = null
        }
    }

    companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
    }
}