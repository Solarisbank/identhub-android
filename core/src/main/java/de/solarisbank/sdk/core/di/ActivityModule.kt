package de.solarisbank.sdk.core.di

import android.content.Context
import android.content.SharedPreferences
import de.solarisbank.sdk.core.BaseActivity

class ActivityModule(private val baseActivity: BaseActivity) {
    fun provideContext(): Context {
        return baseActivity
    }

    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("identhub", Context.MODE_PRIVATE)
    }

}