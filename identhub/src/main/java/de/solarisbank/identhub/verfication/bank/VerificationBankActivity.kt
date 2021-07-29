package de.solarisbank.identhub.verfication.bank

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubActivity
import de.solarisbank.identhub.di.IdentHubActivitySubcomponent
import de.solarisbank.identhub.domain.navigation.router.COMPLETED_STEP
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.identhub.ui.SolarisIndicatorView
import de.solarisbank.identhub.ui.StepIndicator
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.domain.navigation.NaviDirection
import timber.log.Timber

class VerificationBankActivity : IdentHubActivity() {
    private lateinit var viewModel: VerificationBankViewModel
    private lateinit var stepIndicator: StepIndicator
    private var stepIndicatorStep = SolarisIndicatorView.FIRST_STEP
    private var stepIndicatorVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_bank)
        observeViewModel()
        loadState(savedInstanceState)
        Timber.d("intent.getStringExtra(IdentHub.SESSION_URL_KEY): ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initGraph(savedInstanceState)
    }

    private fun initGraph(savedInstanceState: Bundle?) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navInflater = navHostFragment!!.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.bank_nav_graph)
        navHostFragment.navController.setGraph(navGraph, intent.extras)
        initView(savedInstanceState)
    }

    private fun initView(savedInstanceState: Bundle?) {
        stepIndicator = findViewById(R.id.stepIndicator)

        if (savedInstanceState == null) {
            val lastCompletedStep = viewModel.getLastCompletedStep()
            var startStep = COMPLETED_STEP.VERIFICATION_PHONE.index
            if (!IdentHubSession.hasPhoneVerification) {
                startStep = COMPLETED_STEP.VERIFICATION_BANK.index
            }
            stepIndicatorStep = lastCompletedStep?.index ?: startStep
            stepIndicatorVisible = true
        }
        updateStepIndicator()
        setTitle(R.string.identity_activity_second_step_label)
    }

    override fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this)
    }

    private fun observeViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(VerificationBankViewModel::class.java)
        viewModel.getNaviDirectionEvent().observe(this, Observer { event: Event<NaviDirection> -> onNavigationChanged(event) })
        viewModel.cancelState.observe(this, Observer { onCancel(it) })
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume() $this")
    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        val naviDirection = event.content
        Timber.d("onNavigationChanged, naviDirection : $naviDirection")

        if (naviDirection != null) {
            val naviActionId = naviDirection.actionId
            viewModel.doOnNavigationChanged(naviActionId)

            when (naviActionId) {
                IdentHubSession.ACTION_NEXT_STEP -> quit(naviDirection.args!!)
                IdentityActivityViewModel.ACTION_STOP_WITH_RESULT -> quit(naviDirection.args)
                else -> {
                    setSubStep(naviDirection)
                    Navigation
                            .findNavController(this, R.id.nav_host_fragment)
                            .navigate(naviActionId, naviDirection.args)
                }
            }
        }
    }

    private fun setSubStep(naviDirection: NaviDirection) {
      if (naviDirection.actionId == R.id.action_verificationBankIntroFragment_to_verificationBankIbanFragment ||
            naviDirection.actionId == R.id.action_verificationBankFragment_to_establishConnectionFragment ||
            naviDirection.actionId == R.id.action_verificationBankExternalGatewayFragment_to_processingVerificationFragment) {
            stepIndicatorStep = SolarisIndicatorView.SECOND_STEP
        } else if (naviDirection.actionId == R.id.action_processingVerificationFragment_to_contractSigningPreviewFragment) {
            stepIndicatorStep = SolarisIndicatorView.THIRD_STEP
        }

        stepIndicatorVisible = when (naviDirection.actionId) {
            R.id.action_establishConnectionFragment_to_verificationBankExternalGatewayFragment
            -> {
                false
            }
            else -> true
        }

        updateStepIndicator()
    }

    private fun updateStepIndicator() {
        stepIndicator.visibility = if (stepIndicatorVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
        stepIndicator.setStep(stepIndicatorStep)
    }

    private fun onCancel(state: Boolean?) {
        if(state == true) {
            quit(null)
        }
    }

    private fun loadState(saved: Bundle?) {
        saved?.let {
            stepIndicatorStep = it.getInt(KEY_STEP)
            stepIndicatorVisible = it.getBoolean(KEY_STEP_VISIBLE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_STEP, stepIndicatorStep)
        outState.putBoolean(KEY_STEP_VISIBLE, stepIndicatorVisible)
    }

    companion object {
        const val KEY_STEP = "StepIndicatorStep"
        const val KEY_STEP_VISIBLE = "StepIndicatorVisible"
    }
}