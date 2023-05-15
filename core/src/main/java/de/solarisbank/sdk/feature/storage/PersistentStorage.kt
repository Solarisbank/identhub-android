package de.solarisbank.sdk.feature.storage

import android.content.Context
import androidx.core.content.edit

interface PersistentStorage {
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}

class SharedPrefsStorage(context: Context, name: String): PersistentStorage {
    private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit {putBoolean(key, value) }
    }
}