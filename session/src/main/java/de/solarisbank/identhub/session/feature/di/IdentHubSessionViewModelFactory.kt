package de.solarisbank.identhub.session.feature.di

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.feature.di.internal.Factory2

class IdentHubSessionViewModelFactory(
    private val identHubSessionModule: IdentHubSessionModule,
    private val application: Application
): Factory2<ViewModel, SavedStateHandle> {

    override fun create(value: SavedStateHandle): ViewModel {
        return identHubSessionModule.provideIdentHubSessionViewModel(
            value,
            application
        )
    }
}