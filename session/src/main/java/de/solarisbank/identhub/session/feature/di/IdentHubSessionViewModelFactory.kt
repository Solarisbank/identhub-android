package de.solarisbank.identhub.session.feature.di

import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.domain.IdentHubSessionUseCase
import de.solarisbank.sdk.feature.di.internal.Factory

class IdentHubSessionViewModelFactory(
    private val identHubSessionModule: IdentHubSessionModule,
    private val identHubSessionUseCase: IdentHubSessionUseCase
): Factory<ViewModel> {

    override fun get(): ViewModel {
        return identHubSessionModule.provideIdentHubSessionViewModel(identHubSessionUseCase)
    }

    companion object {
        @JvmStatic
        fun create(
            identHubSessionModule: IdentHubSessionModule,
            identHubSessionUseCase: IdentHubSessionUseCase
        ): IdentHubSessionViewModelFactory {
            return IdentHubSessionViewModelFactory(
                    identHubSessionModule,
                    identHubSessionUseCase
            )
        }
    }

}