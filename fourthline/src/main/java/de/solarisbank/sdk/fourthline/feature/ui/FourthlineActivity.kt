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
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.utils.SHOW_UPLOADING_SCREEN
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.feature.view.ConstraintStepIndicator
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineBaseActivity
import de.solarisbank.sdk.fourthline.di.FourthlineActivitySubcomponent
import de.solarisbank.sdk.fourthline.hide
import de.solarisbank.sdk.fourthline.show
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
        stepIndicator.setCurrentStepLabel("ID verification")
        stepIndicator.setPassedStep(3)
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
                    R.id.selfieFragment,
                    R.id.documentScanFragment,
                    R.id.selfieResultFragment -> {
                        toggleTopBars(show = false)
                    }
                    else -> toggleTopBars(show = true)
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
        Timber.d("onNavigationChanged 0; event: ${event}")
        supportActionBar?.setShowHideAnimationEnabled(false)

        event.content?.let {
            when (it) {
                is NaviDirection.FragmentDirection -> {
                    Timber.d("onNavigationChanged 1; it: ${it}")
                    when (it.actionId) {
                        R.id.action_selfieFragment_to_selfieResultFragment -> {
                            Navigation.findNavController(navHostFragment)
                                .navigate(it.actionId, it.args)
                            setTitle(R.string.fourthline_activity_selfie_step_label)
                        }
                        R.id.action_termsAndConditionsFragment_to_welcomeContainerFragment -> {
                            toggleTopBars(show = true)
                            Navigation.findNavController(navHostFragment)
                                .navigate(it.actionId, it.args)
                            setTitle(R.string.fourthline_activity_selfie_step_label)
                        }
                        R.id.action_documentTypeSelectionFragment_to_documentScanFragment -> {
                            Navigation.findNavController(navHostFragment)
                                .navigate(it.actionId, it.args)
                        }
                        R.id.action_documentScanFragment_to_documentResultFragment -> {
                            //toggleTopBars(show = true)
                            Navigation.findNavController(navHostFragment)
                                .navigate(it.actionId, it.args)
                            setTitle(R.string.fourthline_activity_confirm_information_step_label)
                        }
                        R.id.action_welcomeContainerFragment_to_selfieFragment -> {
                            toggleTopBars(show = false)
                            awaitedDirection = it
                            proceedWithPermissions()
                            stepIndicator.setPassedStep(3)
                            setTitle(R.string.fourthline_activity_selfie_step_label)
                        }
                        R.id.action_selfieResultFragment_to_documentTypeSelectionFragment,
                        R.id.action_documentScanFragment_to_documentTypeSelectionFragment -> {
                            stepIndicator.setPassedStep(3)
                            Navigation.findNavController(navHostFragment)
                                .navigate(it.actionId, it.args)
                            setTitle(R.string.fourthline_activity_select_id_step_label)
                        }
                        R.id.action_documentResultFragment_to_kycUploadFragment -> {
                            stepIndicator.setPassedStep(3)
                            Navigation.findNavController(navHostFragment)
                                .navigate(it.actionId, it.args)
                            setTitle(R.string.fourthline_activity_verifying_step_label)
                        }
                        R.id.action_reset_to_welcome_screen -> {
                            stepIndicator.setPassedStep(3)
                            navGraph.startDestination = R.id.welcomeContainerFragment
                            Navigation.findNavController(navHostFragment)
                                .navigate(it.actionId, it.args)
                            setTitle(R.string.fourthline_activity_intro_step_label)
                        }
                        else -> {
                            Navigation.findNavController(navHostFragment)
                                .navigate(it.actionId, it.args)
                            setTitle(R.string.fourthline_activity_intro_step_label)
                        }
                    }
                }

                is SessionStepResult -> {
                    Timber.d("onNavigationChanged 2; it: ${it}")
                    quit(it)
                }

            }
        }
    }

    private fun toggleTopBars(show: Boolean) {
        if (show) {
            supportActionBar?.show()
            stepIndicator.show()
        } else {
            supportActionBar?.hide()
            stepIndicator.hide()
        }
    }

    private fun navigateToAwaitedDirection() {
        Navigation.findNavController(navHostFragment).navigate(awaitedDirection!!.actionId, awaitedDirection!!.args)
        awaitedDirection = null
    }

    private fun requestPermission(permissionCode: Int, rationalizeCode: Int?): Boolean {
        val permission = getPermissionFromCode(permissionCode) ?: return true
        if (ContextCompat.checkSelfPermission(this, permission) != PermissionChecker.PERMISSION_GRANTED) {
            if (permissionCode == rationalizeCode) {
                showRationale(permission)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    permissionCode
                )
            }
            return false
        } else {
            return true
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
                    proceedWithPermissions()
                }
            },
            { viewModel.setFourthlineIdentificationFailure() },
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

    companion object {
        const val FOURTHLINE_IDENTIFICATION_SUCCESSFULL = -10
        const val FOURTHLINE_IDENTIFICATION_ERROR = -20

        private const val PERMISSION_CAMERA_CODE = 32
        private const val PERMISSION_LOCATION_CODE = 52
        private const val PERMISSION_FOREGROUND_CODE = 72

        const val KEY_CODE = "KEY_CODE"
        const val KEY_MESSAGE = "KEY_MESSAGE"
        const val FOURTHLINE_SCAN_FAILED = "ScanFailed"
        const val FOURTHLINE_SELFIE_RETAKE = "SelfieRetake"
    }

}