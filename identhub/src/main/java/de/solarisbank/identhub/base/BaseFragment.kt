package de.solarisbank.identhub.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.AssistedViewModelFactory
import de.solarisbank.identhub.di.FragmentComponent

abstract class BaseFragment : Fragment() {
    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityComponent = (requireActivity() as BaseActivity).activityComponent
        inject(FragmentComponent.Initializer.init(activityComponent))
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    protected open fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
    }

    protected abstract fun inject(component: FragmentComponent)
}