package de.solarisbank.sdk.fourthline.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.domain.ip.IpObtainingUseCase
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.domain.kyc.storage.KycInfoUseCase
import de.solarisbank.sdk.fourthline.domain.kyc.upload.KycUploadUseCase
import de.solarisbank.sdk.fourthline.domain.location.LocationUseCase
import de.solarisbank.sdk.fourthline.domain.person.PersonDataUseCase
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModelFactory
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModelFactory
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadViewModelFactory
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsViewModel
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsViewModelFactory
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomeSharedViewModel
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomeViewModelFactory

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class FourthlineSaveStateViewModelMapProvider private constructor(
        private val fourthlineModule: FourthlineModule,
        private val personDataUseCaseProvider: Provider<PersonDataUseCase>,
        private val kycInfoUseCaseProvider: Provider<KycInfoUseCase>,
        private val locationUseCaseProvider: Provider<LocationUseCase>,
        private val ipObtainingUseCaseProvider: Provider<IpObtainingUseCase>,
        private val kycUploadUseCaseProvider: Provider<KycUploadUseCase>
) : Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> {

    override fun get(): Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>> {
        return linkedMapOf(
                FourthlineViewModel::class.java to FourthlineViewModelFactory.create(fourthlineModule),
                TermsAndConditionsViewModel::class.java to TermsAndConditionsViewModelFactory.create(fourthlineModule),
                WelcomeSharedViewModel::class.java to WelcomeViewModelFactory.create(fourthlineModule),
                KycSharedViewModel::class.java to KycSharedViewModelFactory.create(
                        fourthlineModule,
                        personDataUseCaseProvider,
                        kycInfoUseCaseProvider,
                        locationUseCaseProvider,
                        ipObtainingUseCaseProvider
                ),
            KycUploadViewModel::class.java to KycUploadViewModelFactory.create(fourthlineModule, kycUploadUseCaseProvider)

        )
    }

    companion object {
        fun create(
                fourthlineModule: FourthlineModule,
                personDataUseCaseProvider: Provider<PersonDataUseCase>,
                kycInfoUseCaseProvider: Provider<KycInfoUseCase>,
                locationUseCaseProvider: Provider<LocationUseCase>,
                ipObtainingUseCaseProvider: Provider<IpObtainingUseCase>,
                kycUploadUseCaseProvider: Provider<KycUploadUseCase>
        ): FourthlineSaveStateViewModelMapProvider {
            return FourthlineSaveStateViewModelMapProvider(
                fourthlineModule,
                personDataUseCaseProvider,
                kycInfoUseCaseProvider,
                locationUseCaseProvider,
                ipObtainingUseCaseProvider,
                kycUploadUseCaseProvider
            )
        }
    }
}