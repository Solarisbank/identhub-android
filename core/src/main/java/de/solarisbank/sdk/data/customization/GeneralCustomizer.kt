package de.solarisbank.sdk.data.customization

import android.view.View
import androidx.lifecycle.LifecycleOwner

/**
 * This interface can be used to define classes that do general customization.
 */
interface GeneralCustomizer {
    /**
     * This method is used in BaseFragment when the view is created.
     * @param className javaClass.name of the Fragment that is getting customized. Can be used to
     * do different customizations based on class.
     * @param view The view of the fragment that is getting customized
     * @param viewLifeCycle: The lifecycle of the view of the Fragment that is getting customized.
     * Can be used to observe the lifecycle and for example clear any potential references when
     * onDestroyView is called.
     */
    fun customize(className: String, view: View, viewLifeCycle: LifecycleOwner)
}