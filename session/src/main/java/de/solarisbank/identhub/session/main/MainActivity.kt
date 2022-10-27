package de.solarisbank.identhub.session.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.session.StartIdenthubContract
import de.solarisbank.identhub.session.R
import de.solarisbank.identhub.session.feature.utils.buildApiUrl
import de.solarisbank.sdk.data.di.koin.IdentHubKoinContext
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.feature.alert.AlertDialogFragment
import de.solarisbank.sdk.feature.alert.AlertViewModel
import de.solarisbank.sdk.feature.alert.showAlertFragment
import de.solarisbank.sdk.logger.IdLogger
import de.solarisbank.sdk.module.abstraction.IdenthubModule
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.component.get

class MainActivity : AppCompatActivity(), IdenthubKoinComponent {
    private var currentNavigationId: Int? = null

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
    private val navController: NavController
        get() {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as NavHostFragment
            return navHostFragment.navController
        }

    private var closeButton: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpKoin()
        restoreState(savedInstanceState)
        setContentView(R.layout.identhub_activity_main)
        setUpView()
        observeViewModel()
    }

    private fun setUpKoin() {
        val sessionUrl = intent.getStringExtra(StartIdenthubContract.SessionUrlKey)
        IdentHubKoinContext.setUpKoinApp(this, buildApiUrl(sessionUrl!!))
        loadModules(listOf(MainKoin.module))
    }

    private fun setUpView() {
        closeButton = findViewById(R.id.img_close)
        closeButton?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.state().observe(this) {
            setCurrentModule(it.currentModule)
        }
        viewModel.events().observe(this, ::handleEvent)
    }

    private fun setCurrentModule(module: IdenthubModule) {
        updateNavigationGraph(module.navigationResId)
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

    override fun onBackPressed() {
        showAlertFragment(
            title = getString(R.string.identhub_identity_dialog_quit_process_title),
            message = getString(R.string.identhub_identity_dialog_quit_process_message),
            positiveLabel = getString(R.string.identhub_identity_dialog_quit_process_positive_button),
            negativeLabel = getString(R.string.identhub_identity_dialog_quit_process_negative_button),
            positiveAction = {
                IdLogger.info("User quits the SDK")
                viewModel.onAction(MainAction.CloseTapped)
            },
            tag = "BackButtonAlert")
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
        IdLogger.nav("Activity OnDestroy ${this::class.java}")
    }

    companion object {
        const val KEY_NAVIGATION_ID = "key_navigation_id"
    }
}
