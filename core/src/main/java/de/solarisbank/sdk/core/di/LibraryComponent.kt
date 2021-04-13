package de.solarisbank.sdk.core.di

import android.content.Context

interface LibraryComponent {

    fun activityComponent(): CoreActivityComponent.Factory

    fun applicationContext(): Context
}


