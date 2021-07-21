package de.solarisbank.sdk.fourthline.feature.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.identhub.session.utils.SHOW_UPLOADING_SCREEN
import de.solarisbank.identhub.ui.FourStepIndicatorView
import de.solarisbank.identhub.ui.SolarisIndicatorView
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineBaseActivity
import de.solarisbank.sdk.fourthline.di.FourthlineActivitySubcomponent
import timber.log.Timber

class FourthlineActivity : FourthlineBaseActivity() {

    private lateinit var viewModel: FourthlineViewModel

    private lateinit var navHostFragment: View
    private lateinit var stepIndicator: FourStepIndicatorView

    private var awaitedDirection: NaviDirection? = null
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    override fun inject(activitySubcomponent: FourthlineActivitySubcomponent) {
        activitySubcomponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourthline)
        Timber.d("intent: $intent")
        Timber.d("intent.getStringExtra(IdentHub.SESSION_URL_KEY): ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initView()
        observeViewModel()
        initGraph()
    }

    private fun initView() {
        navHostFragment = findViewById(R.id.nav_host_fragment)
        stepIndicator = findViewById(R.id.stepIndicator)
        stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
    }

    private fun initGraph() {
        navController = (
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment
                ) as NavHostFragment).navController
        val navInflater: NavInflater = navController.navInflater
        navGraph = navInflater.inflate(R.navigation.fourthline_navigation)

        if (intent.hasExtra(SHOW_UPLOADING_SCREEN)) {
            navGraph.startDestination = R.id.kycUploadFragment
        }
        navController.graph = navGraph
        navController.addOnDestinationChangedListener{ controller, destination, arguments ->
                when (destination.id) {
                    R.id.documentTypeSelectionFragment -> {
                        stepIndicator.visibility = View.VISIBLE
                    }
                }
        }
    }

    private fun observeViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(FourthlineViewModel::class.java)

        viewModel.navigationActionId.observe(this, Observer { event -> onNavigationChanged(event) })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        Timber.d("onNavigationChanged; event: ${event}")
        event.content?.let {
            when (it.actionId) {
                R.id.action_selfieFragment_to_selfieResultFragment -> {
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_selfie_step_label)
                }
                R.id.action_termsAndConditionsFragment_to_welcomeContainerFragment -> {
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_selfie_step_label)
                }
                R.id.action_documentTypeSelectionFragment_to_documentScanFragment -> {
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_scan_document_step_label)
                }
                R.id.action_documentScanFragment_to_documentResultFragment -> {
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_confirm_information_step_label)
                }
                R.id.action_welcomeContainerFragment_to_selfieFragment -> {
                    awaitedDirection = it
                    proceedWithPermissions()
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    setTitle(R.string.fourthline_activity_selfie_step_label)
                }
                R.id.action_selfieResultFragment_to_documentTypeSelectionFragment -> {
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_select_id_step_label)
                }
                R.id.action_documentResultFragment_to_locationAccessFragment -> {
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_verifying_step_label)
                }
                R.id.action_locationAccessFragment_to_kycUploadFragment -> {
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_verifying_step_label)
                }
                R.id.action_reset_to_welcome_screen -> {
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    navGraph.startDestination = R.id.welcomeContainerFragment
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_intro_step_label)
                }
                FOURTHLINE_IDENTIFICATION_SUCCESSFULL, IdentHubSession.ACTION_NEXT_STEP -> {
                    quit(it.args)
                }
                FOURTHLINE_IDENTIFICATION_ERROR -> {
                    quit(null)
                }
                else -> {
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                    setTitle(R.string.fourthline_activity_intro_step_label)
                }
            }
        }
    }

    private fun navigateToAwaitedDirection() {
        Navigation.findNavController(navHostFragment).navigate(awaitedDirection!!.actionId, awaitedDirection!!.args)
        awaitedDirection = null
    }

    private fun requestPermission(permission: String, rationalize: Boolean): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) != PermissionChecker.PERMISSION_GRANTED) {
            if (rationalize) {
                showRationale(permission)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    PERMISSION_CODE
                )
            }
            return false
        } else {
            return true
        }
    }

    private fun proceedWithPermissions(rationalize: Boolean = false) {
        if (requestPermission(Manifest.permission.CAMERA, rationalize)
            && requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, rationalize)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (requestPermission(Manifest.permission.FOREGROUND_SERVICE, rationalize)) {
                    navigateToAwaitedDirection()
                }
            } else {
                navigateToAwaitedDirection()
            }
        }
    }

    private fun showRationale(permission: String) {
        showAlertFragment(
            getString(R.string.fourthline_permission_rationale_title),
            getString(R.string.fourthline_permission_rationale_message),
            getString(R.string.fourthline_permission_rationale_ok),
            getString(R.string.fourthline_permission_rationale_quit),
            {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", packageName, null)
                    }.also {
                        startActivity(it)
                    }
                } else {
                    proceedWithPermissions(false)
                }
            },
            { viewModel.setFourthlineIdentificationFailure() },
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CODE) {
            proceedWithPermissions(rationalize = true)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        const val FOURTHLINE_IDENTIFICATION_SUCCESSFULL = -10
        const val FOURTHLINE_IDENTIFICATION_ERROR = -20

        private const val PERMISSION_CODE = 32

        const val KEY_CODE = "KEY_ERROR_CODE"
        const val FOURTHLINE_SELFIE_SCAN_FAILED = "SelfieScanFailed"
        const val FOURTHLINE_SELFIE_RETAKE = "SelfieRetake"
    }

}