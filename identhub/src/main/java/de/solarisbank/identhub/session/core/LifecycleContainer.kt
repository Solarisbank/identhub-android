package de.solarisbank.identhub.session.core

import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@RestrictTo(RestrictTo.Scope.LIBRARY)
class LifecycleContainer(val lifecycle: Lifecycle) {

    private val observers: MutableList<LifecycleEventObserver> = mutableListOf()

    fun addObserver(observer: LifecycleEventObserver) {
        lifecycle.addObserver(observer)
        observers.add(observer)
    }

    fun clearObservers() {
        for (observer in observers) {
            lifecycle.removeObserver(observer)
        }
        observers.clear()
    }
}