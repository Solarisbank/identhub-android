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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavInflater
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.navigation.router.SHOW_STEP_INDICATOR
import de.solarisbank.identhub.session.feature.utils.SHOW_UPLOADING_SCREEN
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.feature.view.ConstraintStepIndicator
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineBaseActivity
import de.solarisbank.sdk.fourthline.di.FourthlineActivitySubcomponent
import de.solarisbank.sdk.fourthline.hide
import de.solarisbank.sdk.fourthline.show
import de.solarisbank.sdk.fourthline.toFourthlineStepParametersDto
import timber.log.Timber

class FourthlineActivity : FourthlineBaseActivity() {

    private lateinit var viewModel: FourthlineViewModel

    private lateinit var navHostFragment: View
    private lateinit var stepIndicator: ConstraintStepIndicator

    private var awaitedDirection: NaviDirection.FragmentDirection? = null
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    override fun inject(activitySubcomponent: FourthlineActivitySubcomponent) {
        activitySubcomponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.identhub_activity_fourthline)
        Timber.d("intent: $intent")
        Timber.d("Intent: SESSION_URL_KEY: ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initView()
        observeViewModel()
        initGraph()
        viewModel.saveFourthlineStepParameters(intent.toFourthlineStepParametersDto())
    }

    private fun initView() {
        navHostFragment = findViewById(R.id.nav_host_fragment)
        initStepIndicator()
        supportActionBar?.setShowHideAnimationEnabled(false)
    }

    private fun initStepIndicator() {
        stepIndicator = findViewById(R.id.stepIndicator)
        if (!shouldShowStepIndicator) {
            stepIndicator.hide()
        } else {
            stepIndicator.customize(customizationRepository.get())
            stepIndicator.setCurrentStepLabelRes(R.string.identhub_stepindicator_verification_id_label)
            stepIndicator.setPassedStep(3)
        }
    }

    private fun initGraph() {
        navController = (
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment
                ) as NavHostFragment).navController
        val navInflater: NavInflater = navController.navInflater
        navGraph = navInflater.inflate(R.navigation.identhub_fourthline_navigation)

        if (intent.hasExtra(SHOW_UPLOADING_SCREEN)) {
            navGraph.startDestination = R.id.kycUploadFragment
        }
        navController.graph = navGraph
        setUpDestinationListener()
    }

    private fun observeViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(FourthlineViewModel::class.java)

        viewModel.navigationActionId.observe(this, ::onNavigationEvent)
    }

    override fun onNewIntent(intent: Intent?) {
        Timber.d("onNewIntent, intent : $intent")
        super.onNewIntent(intent)
    }

    private fun onNavigationEvent(event: Event<NaviDirection>) {
        Timber.d("onNavigationEvent 0; event: $event")

        event.content?.let {
            when (it) {
                is NaviDirection.FragmentDirection -> {
                    Timber.d("onNavigationEvent 1; it: $it")
                    navigate(it)
                }
                is SessionStepResult -> {
                    Timber.d("onNavigationEvent 2; it: $it")
                    quit(it)
                }
                else -> {
                    Timber.d("onNavigationEvent 3; it: $it")
                }
            }
        }
    }

    private fun navigate(direction: NaviDirection.FragmentDirection) {
        when (direction.actionId) {
            R.id.action_selfieInstructionsFragment_to_selfieFragment,
            R.id.action_documentTypeSelectionFragment_to_documentScanFragment -> {
                awaitedDirection = direction
                proceedWithPermissions()
            }
            else -> {
                Navigation.findNavController(navHostFragment)
                    .navigate(direction.actionId, direction.args)
            }
        }
    }

    private fun setUpDestinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.termsAndConditionsFragment -> {
                    toggleTopBars(show = true)
                    setTitle(R.string.identhub_fourthline_activity_intro_step_label)
                }
                R.id.documentTypeSelectionFragment -> {
                    toggleTopBars(show = true)
                    setTitle(R.string.identhub_fourthline_activity_select_id_step_label)
                }
                R.id.documentScanFragment -> {
                    toggleTopBars(show = false)
                }
                R.id.documentResultFragment -> {
                    toggleTopBars(show = true)
                    setTitle(R.string.identhub_fourthline_activity_confirm_information_step_label)
                }
                R.id.selfieInstructionsFragment -> {
                    toggleTopBars(show = true)
                    setTitle(R.string.identhub_fourthline_activity_selfie_step_label)
                }
                R.id.selfieFragment,
                R.id.selfieResultFragment-> {
                    toggleTopBars(show = false)
                }
                R.id.kycUploadFragment,
                R.id.uploadResultFragment-> {
                    toggleTopBars(show = true)
                    setTitle(R.string.identhub_fourthline_activity_verifying_step_label)
                }
                else -> {
                    toggleTopBars(show = true)
                    setTitle(R.string.identhub_fourthline_activity_intro_step_label)
                }
            }
        }
    }

    private fun toggleTopBars(show: Boolean) {
        if (show) {
            supportActionBar?.show()
            if (shouldShowStepIndicator)
                stepIndicator.show()
        } else {
            supportActionBar?.hide()
            stepIndicator.hide()
        }
    }

    private fun navigateToAwaitedDirection() {
        awaitedDirection?.let {
            Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
        }
        awaitedDirection = null
    }

    private fun requestPermission(permissionCode: Int, rationalizeCode: Int?): Boolean {
        val permission = getPermissionFromCode(permissionCode) ?: return true
        return if (ContextCompat.checkSelfPermission(this, permission) != PermissionChecker.PERMISSION_GRANTED) {
            if (permissionCode == rationalizeCode) {
                showRationale(permission)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    permissionCode
                )
            }
            false
        } else {
            true
        }
    }

    private fun proceedWithPermissions(rationalizeCode: Int? = null) {
        if (requestPermission(PERMISSION_CAMERA_CODE, rationalizeCode)
            && requestPermission(PERMISSION_LOCATION_CODE, rationalizeCode)
            && requestPermission(PERMISSION_FOREGROUND_CODE, rationalizeCode)) {
                navigateToAwaitedDirection()
        }
    }

    private fun showRationale(permission: String) {
        showAlertFragment(
            title = getString(R.string.identhub_fourthline_permission_rationale_title),
            message = getString(R.string.identhub_fourthline_permission_rationale_message),
            negativeLabel = getString(R.string.identhub_fourthline_permission_rationale_ok),
            positiveLabel = getString(R.string.identhub_fourthline_permission_rationale_quit),
            negativeAction = {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", packageName, null)
                    }.also {
                        startActivity(it)
                    }
                } else {
                    proceedWithPermissions()
                }
            },
            positiveAction = {
                viewModel.setFourthlineIdentificationFailure()
            }
        )
    }

    private fun getPermissionFromCode(permissionCode: Int): String? {
        return when (permissionCode) {
            PERMISSION_CAMERA_CODE -> Manifest.permission.CAMERA
            PERMISSION_LOCATION_CODE -> Manifest.permission.ACCESS_FINE_LOCATION
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                    permissionCode == PERMISSION_FOREGROUND_CODE) {
                    Manifest.permission.FOREGROUND_SERVICE
                } else {
                    return null
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_LOCATION_CODE, PERMISSION_CAMERA_CODE, PERMISSION_FOREGROUND_CODE -> {
                proceedWithPermissions(requestCode)
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private val shouldShowStepIndicator: Boolean
        get() = intent.getBooleanExtra(SHOW_STEP_INDICATOR, true)

    companion object {
        private const val PERMISSION_CAMERA_CODE = 32
        private const val PERMISSION_LOCATION_CODE = 52
        private const val PERMISSION_FOREGROUND_CODE = 72

        const val KEY_CODE = "KEY_CODE"
        const val KEY_MESSAGE = "KEY_MESSAGE"
        const val FOURTHLINE_SCAN_FAILED = "ScanFailed"
        const val FOURTHLINE_SELFIE_RETAKE = "SelfieRetake"
    }

}