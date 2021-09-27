package de.solarisbank.sdk.fourthline.di

import de.solarisbank.sdk.feature.di.CoreActivityComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity

interface FourthlineActivitySubcomponent {

    fun inject(fourthlineActity: FourthlineActivity)

    interface Factory {
        fun create(activityComponent: CoreActivityComponent): FourthlineActivitySubcomponent
    }

    fun fragmentComponent(): FourthlineFragmentComponent.Factory
}