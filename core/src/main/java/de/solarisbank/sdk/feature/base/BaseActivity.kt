package de.solarisbank.sdk.feature.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.sdk.core_ui.data.dto.Customization
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.feature.alert.AlertDialogFragment
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.di.CoreActivityComponent
import de.solarisbank.sdk.feature.di.DiLibraryComponent
import de.solarisbank.sdk.feature.di.LibraryComponent
import de.solarisbank.sdk.feature.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.logger.IdLogger

abstract class BaseActivity : AppCompatActivity() {

    val libraryComponent: LibraryComponent by lazy {
        DiLibraryComponent.getInstance(application)
    }

    lateinit var assistedViewModelFactory: AssistedViewModelFactory

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var activityComponent: CoreActivityComponent

    lateinit var customizationRepository: CustomizationRepository
    val customization: Customization by lazy { customizationRepository.get() }

    private val alertViewModel: AlertViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AlertViewModel::class.java)
    }

    private var alertDialogFragment: DialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectMe()
        super.onCreate(savedInstanceState)
        initViewModel()
        IdLogger.nav("Activity OnCreate ${this::class.java}")
    }

    protected open fun injectMe() {
        activityComponent = libraryComponent.activityComponent().create(this)
    }

    protected fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, intent.extras)
    }

    fun showAlertFragment(
        title: String,
        message: String,
        positiveLabel: String = "Ok",
        negativeLabel: String? = null,
        positiveAction: () -> Unit,
        negativeAction: (() -> Unit)? = null,
        cancelAction: (() -> Unit)? = null,
        tag: String = AlertDialogFragment.TAG
    ) {
        alertDialogFragment = de.solarisbank.sdk.feature.alert.showAlertFragment(
            title,
            message,
            positiveLabel,
            negativeLabel,
            positiveAction,
            negativeAction,
            cancelAction,
            tag,
            alertViewModel,
            this,
            supportFragmentManager
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialogFragment?.dismissAllowingStateLoss()
        alertDialogFragment = null
        IdLogger.nav("Activity OnDestroy ${this::class.java}")
    }

    companion object {
        const val IDENTHUB_STEP_ACTION = "IDENTHUB_STEP_ACTION"
    }
}
