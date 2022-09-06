package de.solarisbank.sdk.data.utils

import androidx.lifecycle.MutableLiveData

inline fun <T> MutableLiveData<T>.update(updater: T.() -> T?) {
    value = value?.updater()
}