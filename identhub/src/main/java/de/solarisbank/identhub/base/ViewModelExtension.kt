package de.solarisbank.identhub.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

val BaseActivity.defaultViewModelProviderFactory: ViewModelProvider.Factory
    get() {
        return viewModelFactory
    }


inline fun <reified VM : ViewModel> BaseFragment.viewModels() =
        ViewModelProvider(this, this.viewModelFactory)
                .get(VM::class.java)

inline fun <reified VM : ViewModel> BaseFragment.activityViewModels() =
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(VM::class.java)
