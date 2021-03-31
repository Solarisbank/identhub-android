package de.solarisbank.sdk.fourthline.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.fourthline.FourthlineModule
import de.solarisbank.sdk.fourthline.selfie.SelfieSharedViewModel
import de.solarisbank.sdk.fourthline.selfie.SelfieSharedViewModelFactory
import de.solarisbank.sdk.fourthline.terms.TermsAndConditionsViewModel
import de.solarisbank.sdk.fourthline.terms.TermsAndConditionsViewModelFactory
import de.solarisbank.sdk.fourthline.welcome.WelcomeSharedViewModel
import de.solarisbank.sdk.fourthline.welcome.WelcomeViewModelFactory

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class SaveStateViewModelMapProvider(private val fourthlineModule: FourthlineModule) : Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> {

    override fun get(): Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>> {
        return linkedMapOf(
                TermsAndConditionsViewModel::class.java to TermsAndConditionsViewModelFactory.create(fourthlineModule),
                WelcomeSharedViewModel::class.java to WelcomeViewModelFactory.create(fourthlineModule),
                SelfieSharedViewModel::class.java to SelfieSharedViewModelFactory.create(fourthlineModule)
        )
    }

    companion object {
        fun create(fourthlineModule: FourthlineModule): SaveStateViewModelMapProvider {
            return SaveStateViewModelMapProvider(fourthlineModule)
        }
    }
}