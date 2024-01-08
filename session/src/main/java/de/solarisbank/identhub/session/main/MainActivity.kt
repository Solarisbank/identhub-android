package de.solarisbank.identhub.session.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.session.R
import de.solarisbank.identhub.R as coreR
import de.solarisbank.sdk.data.StartIdenthubConfig
import de.solarisbank.identhub.session.StartIdenthubContract
import de.solarisbank.sdk.data.di.koin.IdentHubKoinContext
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.identhub.session.module.customSdkModule
import de.solarisbank.sdk.data.utils.parcelable
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.feature.alert.AlertDialogFragment
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.alert.showAlertFragment
import de.solarisbank.sdk.logger.IdLogger
import de.solarisbank.sdk.module.abstraction.IdenthubFlow
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.get

class MainActivity : AppCompatActivity(), IdenthubKoinComponent {
    private var currentNavigationId: Int? = null

    private val hideBackButtonFragments = listOf("DocumentScanFragment", "SelfieFragment")
    private val hideCloseButtonFragments = listOf(
        "VerificationPhoneSuccessMessageFragment",
        "DocumentScanFragment",
        "SelfieFragment",
        "SelfieResultFragment",
        "UploadResultFragment")

    private val viewModel: MainViewModel by lazy { getViewModel() }
    private val alertViewModel: AlertViewModel by lazy {
        ViewModelProvider(this, object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AlertViewModel(get()) as T
            }
        })[AlertViewModel::class.java]
    }

    private var alertDialogFragment: DialogFragment? = null
    private val navController: NavController by lazy {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment
            val controller = navHostFragment.navController
            controller.addOnDestinationChangedListener { _, destination, _ ->
                IdLogger.nav("Destination changed: ${destination.label}")
                backButton?.isVisible = shouldShowBackButton(destination)
                closeButton?.visibility =
                    if (shouldHideCloseButton(destination)) View.INVISIBLE else View.VISIBLE
                onBackPressedCallback.isEnabled = !shouldShowBackButton(destination)
            }
            controller
        }

    private fun shouldShowBackButton(destination: NavDestination?) =
        hideBackButtonFragments.contains(destination?.label)

    private fun shouldHideCloseButton(destination: NavDestination?) =
        hideCloseButtonFragments.contains(destination?.label)

    private var closeButton: AppCompatImageView? = null
    private var backButton: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpKoin()
        IdLogger.nav("Activity onCreate: ${this::class.java.simpleName}")
        restoreState(savedInstanceState)
        setContentView(R.layout.identhub_activity_main)
        setUpView()
        observeViewModel()
        addBackPressedCallback()
    }

    private fun setUpKoin() {
        val startConfig: StartIdenthubConfig? = intent.parcelable(StartIdenthubContract.ConfigKey)
        IdentHubKoinContext.setUpKoinApp(this, startConfig!!)
        loadModules(listOf(MainKoin.module, customSdkModule))
    }

    private fun setUpView() {
        closeButton = findViewById(R.id.img_close)
        closeButton?.setOnClickListener {
            showQuitDialog()
        }
        backButton = findViewById(R.id.img_back)
        backButton?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.state().observe(this) {
            setCurrentModule(it.currentModule)
        }
        viewModel.events().observe(this, ::handleEvent)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showQuitDialog()
        }
    }

    private fun addBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this,
            onBackPressedCallback
        )
    }

    private fun setCurrentModule(module: IdenthubFlow?) {
        if (module == null) {
            showGenericError()
            return
        }
        updateNavigationGraph(module.navigationResId)
    }

    private fun showGenericError() {
        showAlertFragment(
            title = getString(coreR.string.identhub_generic_error_title),
            message = getString(coreR.string.identhub_generic_error_message),
            negativeLabel = getString(coreR.string.identhub_generic_retry_button),
            negativeAction = { viewModel.onAction(MainAction.RetryTapped) },
            positiveLabel = getString(coreR.string.identhub_identity_dialog_quit_process_positive_button),
            positiveAction = ::close
        )
    }

    private fun updateNavigationGraph(navigationId: Int) {
        if (currentNavigationId == navigationId) return
        currentNavigationId = navigationId
        navController.graph = navController.navInflater.inflate(navigationId)
    }

    private fun handleEvent(event: Event<MainViewEvent>) {
        val content = event.content ?: return

        when (content) {
            is MainViewEvent.Navigate -> {
                navController.navigate(content.navigationId, args = content.bundle)
            }
            is MainViewEvent.Close -> {
                setResult(
                    RESULT_OK,
                    Intent().putExtra(StartIdenthubContract.ResultKey, content.result.toBundle())
                )
                finish()
                IdentHubKoinContext.clear()
            }
        }
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
        alertDialogFragment = showAlertFragment(
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

    private fun showQuitDialog() {
        showAlertFragment(
            title = getString(coreR.string.identhub_identity_dialog_quit_process_title),
            message = getString(coreR.string.identhub_identity_dialog_quit_process_message),
            positiveLabel = getString(coreR.string.identhub_identity_dialog_quit_process_positive_button),
            negativeLabel = getString(coreR.string.identhub_identity_dialog_quit_process_negative_button),
            positiveAction = ::close,
            tag = "BackButtonAlert")
    }

    private fun close() {
        IdLogger.info("User quits the SDK")
        viewModel.onAction(MainAction.CloseTapped)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentNavigationId?.let {
            outState.putInt(KEY_NAVIGATION_ID, it)
        }
    }

    private fun restoreState(saved: Bundle?) {
        saved ?: return
        currentNavigationId = saved.getInt(KEY_NAVIGATION_ID)

        currentNavigationId?.let {
            updateNavigationGraph(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialogFragment?.dismissAllowingStateLoss()
        alertDialogFragment = null
        IdLogger.nav("Activity onDestroy: ${this::class.java.simpleName}")
    }

    companion object {
        const val KEY_NAVIGATION_ID = "key_navigation_id"
    }
}
