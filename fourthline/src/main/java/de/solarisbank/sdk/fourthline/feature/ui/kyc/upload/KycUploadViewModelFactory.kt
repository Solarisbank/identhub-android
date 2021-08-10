package de.solarisbank.sdk.fourthline.feature.ui.kyc.upload

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.di.FourthlineModule
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase

class KycUploadViewModelFactory private constructor(
    private val fourthlineModule: FourthlineModule,
    private val kycUploadUseCase: KycUploadUseCase
) : Factory2<ViewModel, SavedStateHandle>{

    override fun create(value: SavedStateHandle?): ViewModel {
        return fourthlineModule.provideKycUploadViewModel(kycUploadUseCase)
    }

    companion object {
        fun create(
            fourthlineModule: FourthlineModule,
            kycUploadUseCaseProvider: Provider<KycUploadUseCase>
        ): KycUploadViewModelFactory {
            return KycUploadViewModelFactory(fourthlineModule, kycUploadUseCaseProvider.get())
        }
    }
}