package de.solarisbank.sdk.core

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

abstract class BaseFragment : Fragment() {
    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    protected open fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
    }


}