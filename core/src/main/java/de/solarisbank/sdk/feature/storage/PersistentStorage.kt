package de.solarisbank.sdk.feature.storage

import android.content.Context
import androidx.core.content.edit
import com.squareup.moshi.Moshi

interface PersistentStorage {
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)

    fun putString(key: String, value: String?)

    fun getString(key: String): String?

    fun <T> put(key: String, clazz: Class<T>, value: T)

    fun <T> get(key: String, clazz: Class<T>): T?

}

inline fun <reified T> PersistentStorage.get(key: String): T? {
    return get(key, T::class.java)
}

inline fun <reified T> PersistentStorage.put(key: String, value: T) {
    put(key, T::class.java, value)
}

class SharedPrefsStorage(
    context: Context,
    name: String,
    private val moshi: Moshi,
) : PersistentStorage {

    private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    override fun putString(key: String, value: String?) {
        prefs.edit { putString(key, value) }
    }

    override fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    override fun <T> put(key: String, clazz: Class<T>, value: T) {
        putString(key, moshi.adapter(clazz).toJson(value))
    }

    override fun <T> get(key: String, clazz: Class<T>): T? {
        val valueString = getString(key)
        return if (valueString == null)
            null
        else
            moshi.adapter(clazz).fromJson(valueString)
    }
}