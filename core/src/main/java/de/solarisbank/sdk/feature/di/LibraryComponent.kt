package de.solarisbank.sdk.feature.di

import android.content.Context

interface LibraryComponent {

    fun activityComponent(): CoreActivityComponent.Factory

    fun applicationContext(): Context
}


