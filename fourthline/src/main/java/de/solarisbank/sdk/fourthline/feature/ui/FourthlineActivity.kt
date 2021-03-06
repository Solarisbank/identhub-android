package de.solarisbank.sdk.fourthline.feature.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import de.solarisbank.identhub.router.NEXT_STEP_ACTION
import de.solarisbank.identhub.router.NEXT_STEP_KEY
import de.solarisbank.identhub.router.toNextStep
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.identhub.session.utils.SHOW_UPLOADING_SCREEN
import de.solarisbank.identhub.ui.FourStepIndicatorView
import de.solarisbank.identhub.ui.SolarisIndicatorView
import de.solarisbank.identhub.ui.StepIndicator
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
        initViewModel()
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

    override fun initViewModel() {
        super.initViewModel()
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
                R.id.action_welcomeContainerFragment_to_selfieFragment -> {
                    awaitedDirection = it
                    requestCameraPermission()
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                }
                R.id.action_selfieResultFragment_to_documentTypeSelectionFragment -> {
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                }
                R.id.action_documentResultFragment_to_locationAccessFragment -> {
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                }
                R.id.action_locationAccessFragment_to_kycUploadFragment -> {
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                }
                R.id.action_reset_to_welcome_screen -> {
                    stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
                    navGraph.startDestination = R.id.welcomeContainerFragment
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                }
                FOURTHLINE_IDENTIFICATION_SUCCESSFULL -> {
                    quit(it.args)
                }
                IdentHubSession.ACTION_NEXT_STEP -> { //dynamic flow
                    forwardTo(it.args!!)
                }
                else -> {
                    Navigation.findNavController(navHostFragment).navigate(it.actionId, it.args)
                }
            }
        }
    }

    private fun navigateToAwaitedDirection() {
        Navigation.findNavController(navHostFragment).navigate(awaitedDirection!!.actionId, awaitedDirection!!.args)
        awaitedDirection = null
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 32)
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        else {
            requestForegroundServicePermission()
        }
    }

    private fun requestForegroundServicePermission() {
        Timber.d("requestForegroundServicePermission()")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.FOREGROUND_SERVICE), FOREGROUND_SERVICE_PERMISSION_CODE)
        } else {
            navigateToAwaitedDirection()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PermissionChecker.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission is required to proceed", Toast.LENGTH_SHORT)
                        .show()
                requestCameraPermission()
            } else {
                requestLocationPermission()
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PermissionChecker.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission is required to proceed", Toast.LENGTH_SHORT)
                        .show()
                requestLocationPermission()
            } else {
                requestForegroundServicePermission()
            }
        } else if (requestCode == FOREGROUND_SERVICE_PERMISSION_CODE && grantResults.isEmpty() || grantResults[0] != PermissionChecker.PERMISSION_GRANTED) {
            requestForegroundServicePermission()
        } else {
            navigateToAwaitedDirection()
        }
    }

    private fun forwardTo(args: Bundle) {
        val nextStep = args.getString(NEXT_STEP_KEY)
        val forwardIntent = toNextStep(this, nextStep!!)
        forwardIntent.action = NEXT_STEP_ACTION
        forwardIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        forwardIntent.putExtra(IdentHub.SESSION_URL_KEY, intent.getStringExtra(IdentHub.SESSION_URL_KEY))
        forwardIntent.putExtras(args)
        setResult(Activity.RESULT_OK, forwardIntent)
        finish()
    }

    private fun quit(bundle: Bundle?) {
        var intent: Intent? = null
        if (bundle != null) {
            intent = Intent()
            intent.putExtras(bundle)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {

        const val FOURTHLINE_IDENTIFICATION_SUCCESSFULL = -10
        const val FOURTHLINE_IDENTIFICATION_ERROR = -20

        private const val CAMERA_PERMISSION_CODE = 32
        private const val LOCATION_PERMISSION_CODE = 42
        private const val FOREGROUND_SERVICE_PERMISSION_CODE = 52
    }

}