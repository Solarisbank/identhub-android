package de.solarisbank.identhub.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.AssistedViewModelFactory
import de.solarisbank.identhub.di.ActivityComponent
import de.solarisbank.identhub.di.LibraryComponent

abstract class BaseActivity : AppCompatActivity() {
    lateinit var assistedViewModelFactory: AssistedViewModelFactory
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val activityComponent: ActivityComponent by lazy {
        ActivityComponent.Initializer.init(this, LibraryComponent.getInstance(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectMe()
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    protected open fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, intent.extras)
    }

    private fun injectMe() {
        inject(activityComponent)
    }

    protected abstract fun inject(activityComponent: ActivityComponent)
}