package de.solarisbank.identhub.session.feature.di

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import de.solarisbank.sdk.feature.customization.CustomizationRepository
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class IdentHubSessionViewModelFactory(
    private val identHubSessionModule: IdentHubSessionModule,
    private val identHubSessionUseCase: IdentHubSessionUseCase,
    private val customizationRepositoryProvider: Provider<CustomizationRepository>,
): Factory<ViewModel> {

    override fun get(): ViewModel {
        return identHubSessionModule.provideIdentHubSessionViewModel(
            identHubSessionUseCase,
            customizationRepositoryProvider.get()
        )
    }
}