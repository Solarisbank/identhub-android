package de.solarisbank.sdk.core.di

import android.content.Context
import android.content.SharedPreferences
import de.solarisbank.sdk.core.BaseActivity

interface ActivityComponent {

    fun context(): Context

    fun sharedPreferences(): SharedPreferences

    interface Factory {
        fun create(baseActivity: BaseActivity): ActivityComponent
    }

    fun inject(introActivity: BaseActivity)

}