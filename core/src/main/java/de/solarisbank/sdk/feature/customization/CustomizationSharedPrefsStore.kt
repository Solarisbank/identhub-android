package de.solarisbank.sdk.feature.customization

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class CustomizationSharedPrefsStore(
    val sharedPreferences: SharedPreferences
) {

    val adapter: JsonAdapter<Customization> by lazy {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        moshi.adapter(Customization::class.java)
    }

    @SuppressLint("ApplySharedPref")
    fun put(customization: Customization) {
        val json = adapter.toJson(customization)
        sharedPreferences.edit()
            .putString(CUSTOMIZATION_KEY, json)
            .commit()
    }

    fun get(): Customization? {
        val json = sharedPreferences.getString(CUSTOMIZATION_KEY, null) ?: return null
        return adapter.fromJson(json)
    }

    companion object {
        const val CUSTOMIZATION_KEY = "sdk_customization_key"
    }
}