package de.solarisbank.sdk.fourthline.feature.ui.kyc.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.di.FourthlineModule
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase

class KycSharedViewModelFactory(
        private val fourthlineModule: FourthlineModule,
        private val personDataUseCaseProvider: Provider<PersonDataUseCase>,
        private val kycInfoUseCaseProvider: Provider<KycInfoUseCase>,
        private val locationUseCaseProvider: Provider<LocationUseCase>
) : Factory2<ViewModel, SavedStateHandle> {

    override fun create(value: SavedStateHandle): ViewModel {
        return fourthlineModule.provideDocScanSharedViewModel(
                value,
                personDataUseCaseProvider.get(),
                kycInfoUseCaseProvider.get(),
                locationUseCaseProvider.get()
        )
    }

    companion object {
        @JvmStatic
        fun create(
                fourthlineModule: FourthlineModule,
                personDataUseCaseProvider: Provider<PersonDataUseCase>,
                kycInfoUseCaseProvider: Provider<KycInfoUseCase>,
                locationUseCaseProvider: Provider<LocationUseCase>
        ): KycSharedViewModelFactory {
            return KycSharedViewModelFactory(
                    fourthlineModule,
                    personDataUseCaseProvider,
                    kycInfoUseCaseProvider,
                    locationUseCaseProvider
            )
        }
    }
}