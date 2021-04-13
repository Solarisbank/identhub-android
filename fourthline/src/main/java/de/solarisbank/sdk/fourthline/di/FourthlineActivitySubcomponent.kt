package de.solarisbank.sdk.fourthline.di

import de.solarisbank.sdk.core.di.CoreActivityComponent
import de.solarisbank.sdk.fourthline.FourthlineActivity

interface FourthlineActivitySubcomponent {

    fun inject(fourthlineActity: FourthlineActivity)

    interface Factory {
        fun create(activityComponent: CoreActivityComponent): FourthlineActivitySubcomponent
    }

    fun fragmentComponent(): FourthlineFragmentComponent.Factory
}