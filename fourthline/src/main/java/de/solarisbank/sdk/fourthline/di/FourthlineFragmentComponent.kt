package de.solarisbank.sdk.fourthline.di

import de.solarisbank.sdk.fourthline.feature.ui.kyc.result.UploadResultFragment
import de.solarisbank.sdk.fourthline.feature.ui.kyc.upload.KycUploadFragment
import de.solarisbank.sdk.fourthline.feature.ui.passing.possibility.PassingPossibilityFragment
import de.solarisbank.sdk.fourthline.feature.ui.scan.DocScanFragment
import de.solarisbank.sdk.fourthline.feature.ui.scan.DocScanResultFragment
import de.solarisbank.sdk.fourthline.feature.ui.scan.DocTypeSelectionFragment
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieFragment
import de.solarisbank.sdk.fourthline.feature.ui.selfie.SelfieResultFragment
import de.solarisbank.sdk.fourthline.feature.ui.terms.TermsAndConditionsFragment
import de.solarisbank.sdk.fourthline.feature.ui.terms.welcome.WelcomeContainerFragment
import de.solarisbank.sdk.fourthline.feature.ui.welcome.WelcomePageFragment

interface FourthlineFragmentComponent {

    fun inject(termsAndConditionsFragment: TermsAndConditionsFragment)

    fun inject(welcomeContainerFragment: WelcomeContainerFragment)

    fun inject(welcomePageFragment: WelcomePageFragment)

    fun inject(selfieFragment: SelfieFragment)

    fun inject(selfieResultFragment: SelfieResultFragment)

    fun inject(docTypeSelectionFragment: DocTypeSelectionFragment)

    fun inject(docScanFragment: DocScanFragment)

    fun inject(docScanResultFragment: DocScanResultFragment)

    fun inject(kycUploadFragment: KycUploadFragment)

    fun inject(passingPossibilityFragment: PassingPossibilityFragment)

    fun inject(uploadResultFragment: UploadResultFragment)

    interface Factory {
        fun create(): FourthlineFragmentComponent
    }
}