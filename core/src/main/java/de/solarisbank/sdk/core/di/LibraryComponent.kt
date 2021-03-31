package de.solarisbank.sdk.core.di

import android.content.Context

interface LibraryComponent {

    fun activityComponent(): ActivityComponent.Factory

    fun applicationContext(): Context
}


