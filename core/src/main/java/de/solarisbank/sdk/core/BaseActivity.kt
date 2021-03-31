package de.solarisbank.sdk.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core.di.ActivityComponent
import de.solarisbank.sdk.core.di.DiLibraryComponent
import de.solarisbank.sdk.core.di.LibraryComponent
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory

abstract class BaseActivity : AppCompatActivity() {

    val libraryComponent: LibraryComponent by lazy {
        DiLibraryComponent.getInstance(application)
    }

    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        injectMe()
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    protected open fun injectMe() {
        activityComponent = libraryComponent.activityComponent().create(this)
    }

    protected open fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, intent.extras)
    }
}