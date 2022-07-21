package de.solarisbank.sdk.feature.di

import android.content.Context
import de.solarisbank.sdk.feature.base.BaseActivity

interface CoreActivityComponent {

    fun context(): Context

    interface Factory {
        fun create(baseActivity: BaseActivity): CoreActivityComponent
    }

    fun inject(introActivity: BaseActivity)

}