package de.solarisbank.sdk.feature.di

import android.content.Context
import de.solarisbank.sdk.feature.base.BaseActivity

class ActivityModule(private val baseActivity: BaseActivity) {
    fun provideContext(): Context {
        return baseActivity
    }
}