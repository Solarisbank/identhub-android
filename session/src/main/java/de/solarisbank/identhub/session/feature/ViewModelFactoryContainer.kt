package de.solarisbank.identhub.session.feature

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider

interface ViewModelFactoryContainer {
    var viewModelFactory: (FragmentActivity) -> ViewModelProvider.Factory
}