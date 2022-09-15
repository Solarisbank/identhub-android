package de.solarisbank.identhub.session.main

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import org.koin.core.component.inject

open class NewBaseFragment: Fragment(), IdenthubKoinComponent {
    private val customizationRepository by inject<CustomizationRepository>()
    val customization by lazy(LazyThreadSafetyMode.NONE) { customizationRepository.get() }

    val navigator: Navigator?
    get() {
        return activity?.let {
            ViewModelProvider(it).get(MainViewModel::class.java)
        }
    }
}