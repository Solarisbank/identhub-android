package de.solarisbank.sdk.fourthline.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.sdk.core.di.CoreActivityComponent
import de.solarisbank.sdk.core.di.internal.DoubleCheck
import de.solarisbank.sdk.core.di.internal.Factory2
import de.solarisbank.sdk.core.di.internal.Provider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.FourthlineActivity
import de.solarisbank.sdk.fourthline.FourthlineActivityInjector
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

class FourthlineComponent private constructor(
        val fourthlineModule: FourthlineModule,
        val activitySubModule: FourthlineActivitySubModule
) {

    internal class ContextProvider(private val activityComponent: CoreActivityComponent) : Provider<Context> {
        override fun get(): Context {
            return activityComponent.context()
        }
    }

    fun activitySubcomponent(): FourthlineActivitySubcomponent.Factory {
        return FourthlineActivitySubcomponentFactory()
    }

    inner class FourthlineActivitySubcomponentFactory : FourthlineActivitySubcomponent.Factory {
        override fun create(activityComponent: CoreActivityComponent): FourthlineActivitySubcomponent {
            return FourthlineActivitySubcomponentImpl(activityComponent, fourthlineModule)
        }

    }

    class FourthlineActivitySubcomponentImpl(
            val coreActivityComponent: CoreActivityComponent,
            val fourthlineModule: FourthlineModule
    ) : FourthlineActivitySubcomponent {

        private val contextProvider: Provider<Context> = ContextProvider(coreActivityComponent)
        private var saveStateViewModelMapProvider: Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> =
                FourthlineSaveStateViewModelMapProvider.create(fourthlineModule)
        private var mapOfClassOfAndProviderOfViewModelProvider: Provider<Map<Class<out ViewModel>, Provider<ViewModel>>> =
                DoubleCheck.provider(EmptyMapOfClassOfAndProviderOfViewModelProvider())
        private var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> =
                DoubleCheck.provider(FourthlineModuleAssistedViewModelFactory.create(
                        fourthlineModule,
                        mapOfClassOfAndProviderOfViewModelProvider,
                        saveStateViewModelMapProvider
                ))


        override fun inject(fourthlineActivity: FourthlineActivity) {
            FourthlineActivityInjector.injectAssistedViewModelFactory(fourthlineActivity, assistedViewModelFactoryProvider.get())
        }

        override fun fragmentComponent(): FourthlineFragmentComponent.Factory {
            return FourthlineFragmentComponentFactory()
        }
        inner class FourthlineFragmentComponentFactory : FourthlineFragmentComponent.Factory {
            override fun create(): FourthlineFragmentComponent {
                return FragmentComponentImpl(this@FourthlineActivitySubcomponentImpl.fourthlineModule)
            }
        }

        private inner class FragmentComponentImpl(fourthlineModule: FourthlineModule) : FourthlineFragmentComponent{

            var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> = DoubleCheck.provider(FourthlineModuleAssistedViewModelFactory.create(
                    fourthlineModule, mapOfClassOfAndProviderOfViewModelProvider, saveStateViewModelMapProvider
            ))

            override fun inject(termsAndConditionsFragment: TermsAndConditionsFragment) {
                TermsAndConditionsInjector.injectAssistedViewModelFactory(termsAndConditionsFragment, assistedViewModelFactoryProvider.get())
            }

            override fun inject(welcomeContainerFragment: WelcomeContainerFragment) {
                injectAssistedViewModelFactory(
                        welcomeContainerFragment, assistedViewModelFactoryProvider.get()
                )
            }

            override fun inject(welcomePageFragment: WelcomePageFragment) {
                injectAssistedViewModelFactory(
                        welcomePageFragment, assistedViewModelFactoryProvider.get()
                )
            }

            override fun inject(selfieFragment: SelfieFragment) {
                injectAssistedViewModelFactory(selfieFragment, assistedViewModelFactoryProvider.get())
            }
            override fun inject(selfieResultFragment: SelfieResultFragment) {
                injectAssistedViewModelFactory(selfieResultFragment, assistedViewModelFactoryProvider.get())
            }

        }
    }

    internal class SharedPreferencesProvider(private val activityComponent: CoreActivityComponent) : Provider<SharedPreferences> {
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
        private var fourthlineComponent: FourthlineComponent? = null

        @Synchronized fun getInstance(): FourthlineComponent {
            if (fourthlineComponent == null) {
                fourthlineComponent = FourthlineComponent(
                        FourthlineModule(),
                        FourthlineActivitySubModule()
                )
            }

            return fourthlineComponent!!
        }
    }
}