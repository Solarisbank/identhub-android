package de.solarisbank.sdk.fourthline

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.ActivityComponent
import de.solarisbank.sdk.core.di.internal.DoubleCheck
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.di.FourthlineModuleAssistedViewModelFactory
import de.solarisbank.sdk.fourthline.di.SaveStateViewModelMapProvider
import de.solarisbank.sdk.fourthline.selfie.SelfieFragment
import de.solarisbank.sdk.fourthline.selfie.SelfieFragmentInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.sdk.fourthline.selfie.SelfieResultFragment
import de.solarisbank.sdk.fourthline.selfie.SelfieResultFragmentInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.sdk.fourthline.terms.TermsAndConditionsFragment
import de.solarisbank.sdk.fourthline.terms.TermsAndConditionsInjector
import de.solarisbank.sdk.fourthline.welcome.WelcomeContainerFragment
import de.solarisbank.sdk.fourthline.welcome.WelcomeContainerFragmentInjector.Companion.injectAssistedViewModelFactory
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragment
import de.solarisbank.sdk.fourthline.welcome.WelcomePageFragmentInjector.Companion.injectAssistedViewModelFactory

class FourthlineComponent private constructor(activityComponent: ActivityComponent, fourthlineModule: FourthlineModule) {
    private var contextProvider: Provider<Context> = ContextProvider(activityComponent)
    private var sharedPreferencesProvider: Provider<SharedPreferences> = SharedPreferencesProvider(activityComponent)
    private var mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> = DoubleCheck.provider(EmptyMapOfClassOfAndProviderOfViewModelProvider())

    private var saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> = SaveStateViewModelMapProvider.create(fourthlineModule)
    private var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> = DoubleCheck.provider(FourthlineModuleAssistedViewModelFactory.create(fourthlineModule, mapOfClassOfAndProviderOfViewModelProvider, saveStateViewModelMapProvider))

    internal class ContextProvider(private val activityComponent: ActivityComponent) : Provider<Context> {
        override fun get(): Context {
            return activityComponent.context()
        }
    }

    fun inject(termsAndConditionsFragment: TermsAndConditionsFragment) {
        TermsAndConditionsInjector.injectAssistedViewModelFactory(termsAndConditionsFragment, assistedViewModelFactoryProvider.get())
    }

    fun inject(welcomeContainerFragment: WelcomeContainerFragment) {
        injectAssistedViewModelFactory(
                welcomeContainerFragment, assistedViewModelFactoryProvider.get()
        )
    }

    fun inject(welcomePageFragment: WelcomePageFragment) {
        injectAssistedViewModelFactory(
                welcomePageFragment, assistedViewModelFactoryProvider.get()
        )
    }

    fun inject(selfieFragment: SelfieFragment) {
        injectAssistedViewModelFactory(selfieFragment, assistedViewModelFactoryProvider.get())
    }

    fun inject(selfieResultFragment: SelfieResultFragment) {
        injectAssistedViewModelFactory(selfieResultFragment, assistedViewModelFactoryProvider.get())
    }

    internal class SharedPreferencesProvider(private val activityComponent: ActivityComponent) : Provider<SharedPreferences> {
        override fun get(): SharedPreferences {
            return activityComponent.sharedPreferences()
        }
    }

    internal class EmptyMapOfClassOfAndProviderOfViewModelProvider() : Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> {
        override fun get(): Map<Class<out ViewModel>, Provider<ViewModel>> {
            return emptyMap()
        }
    }

    companion object {
        private val lock = Any()
        private var fourthlineComponent: FourthlineComponent? = null

        fun getInstance(activityComponent: ActivityComponent): FourthlineComponent {
            synchronized(lock) {
                if (fourthlineComponent == null) {
                    fourthlineComponent = FourthlineComponent(activityComponent, FourthlineModule())
                }

                return fourthlineComponent!!
            }
        }
    }
}