package de.solarisbank.identhub.session.core

import android.annotation.SuppressLint
import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentManager

@RestrictTo(RestrictTo.Scope.LIBRARY)
abstract class ActivityResultLauncher<I> {
    abstract fun launch(@SuppressLint("UnknownNullness") input: I, fragmentManager: FragmentManager, stepType: String)
}