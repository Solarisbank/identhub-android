package de.solarisbank.identhub.session.feature.di

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.feature.ViewModelFactoryContainer
import de.solarisbank.identhub.session.feature.viewmodel.IdentHubSessionViewModel
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Factory2
import de.solarisbank.sdk.feature.di.internal.Provider
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory

class IdentHubActivityComponent(val activity: Activity) {

    private val identHubSessionModule: IdentHubSessionModule = IdentHubSessionModule()

    private var saveStateViewModelMapProvider:
            Provider<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> =
        DoubleCheck.provider(object :
            Factory<Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>> {
            override fun get(): Map<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>> {
                return LinkedHashMap<Class<out ViewModel>, Factory2<ViewModel, SavedStateHandle>>()
                    .also {
                        it[IdentHubSessionViewModel::class.java] =
                            IdentHubSessionViewModelFactory(
                                identHubSessionModule,
                                activity.application
                            )
                    }
            }
        })

    private var assistedViewModelFactoryProvider: Provider<AssistedViewModelFactory> =
        DoubleCheck.provider(
            IdentHubeSessionViewModelFactory.create(
                identHubSessionModule,
                saveStateViewModelMapProvider
            )
        )

        fun inject(viewModelFactoryContainer: ViewModelFactoryContainer) {
            viewModelFactoryContainer.viewModelFactory =
                { assistedViewModelFactoryProvider.get().create(it, null) }
        }
}