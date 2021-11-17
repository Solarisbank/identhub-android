package de.solarisbank.sdk.fourthline.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.ip.IpObtainingUseCase
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.domain.kyc.delete.DeleteKycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import de.solarisbank.sdk.fourthline.domain.step.parameters.FourthlineStepParametersUseCase
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadViewModel
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsViewModel
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomeSharedViewModel

@RestrictTo(RestrictTo.Scope.LIBRARY)
class FourthlineModule {

    fun provideViewModelFactory(
        classProviderMap: Map<Class<out ViewModel>, Provider<ViewModel>>,
        classSavedStateProviderMap: Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>
    ): AssistedViewModelFactory {
        return AssistedViewModelFactory(classSavedStateProviderMap, classProviderMap)
    }

    fun provideTermsAndConditionsViewModel(savedStateHandle: SavedStateHandle): ViewModel {
        return TermsAndConditionsViewModel(savedStateHandle)
    }

    fun provideWelcomeSharedViewModel(savedStateHandle: SavedStateHandle): WelcomeSharedViewModel {
        return WelcomeSharedViewModel(savedStateHandle)
    }

    fun provideKycUploadViewModel(kycUploadUseCase: KycUploadUseCase): KycUploadViewModel {
        return KycUploadViewModel(kycUploadUseCase)
    }

    fun provideFourthlineViewModel(
            savedStateHandle: SavedStateHandle,
            fourthlineStepParametersUseCase: FourthlineStepParametersUseCase
    ): ViewModel {
        return FourthlineViewModel(savedStateHandle, fourthlineStepParametersUseCase)
    }

    fun provideDocScanSharedViewModel(
        savedStateHandle: SavedStateHandle,
        personDataUseCase: PersonDataUseCase,
        kycInfoUseCase: KycInfoUseCase,
        locationUseCase: LocationUseCase,
        ipObtainingUseCase: IpObtainingUseCase,
        deleteKycInfoUseCase: DeleteKycInfoUseCase
    ): ViewModel {
        return KycSharedViewModel(
            savedStateHandle,
            personDataUseCase,
            kycInfoUseCase,
            locationUseCase,
            ipObtainingUseCase,
            deleteKycInfoUseCase
        )
    }
}