package de.solarisbank.identhub.identity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubActivity
import de.solarisbank.identhub.di.IdentHubActivitySubcomponent
import de.solarisbank.identhub.session.feature.IdentHubSession
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.navigation.router.COMPLETED_STEP
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.feature.view.ConstraintStepIndicator
import timber.log.Timber

class IdentityActivity : IdentHubActivity() {
    private lateinit var viewModel: IdentityActivityViewModel
    private lateinit var stepIndicator: ConstraintStepIndicator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.identhub_activity_identity)
        initGraph()
        initView()
        observeViewModel()
    }

    fun initGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navInflater = navHostFragment!!.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.identhub_identity_nav_graph)
        if (!IdentHubSession.hasPhoneVerification) {
            navGraph.startDestination = R.id.verificationBankFragment
        }
        val lastCompletedStep = viewModel.lastCompletedStep
        if (lastCompletedStep === COMPLETED_STEP.VERIFICATION_BANK) {
            navGraph.startDestination = R.id.contractSigningPreviewFragment
        }
        navHostFragment.navController.setGraph(navGraph, intent.extras)
    }

    private fun initView() {
        stepIndicator = findViewById(R.id.stepIndicator)
        val lastCompletedStep = viewModel.lastCompletedStep
        var startStep = COMPLETED_STEP.VERIFICATION_PHONE.index
        if (!IdentHubSession.hasPhoneVerification) {
            startStep = COMPLETED_STEP.VERIFICATION_BANK.index
        }
        stepIndicator.setPassedStep(lastCompletedStep?.index ?: startStep)
    }

    override fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this)
    }

    private fun observeViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(IdentityActivityViewModel::class.java)
        viewModel.naviDirectionEvent.observe(
            this
        ) { event: Event<NaviDirection> -> onNavigationChanged(event) }
    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        val naviDirection = event.content
        event.content?.let {
            when (it) {
                is NaviDirection.FragmentDirection -> {
                    Timber.d("onNavigationChanged 1; it: ${it}")
                    when (it.actionId) {
                        R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment -> {
                            stepIndicator.setPassedStep(2)
                        }
                        R.id.action_processingVerificationFragment_to_contractSigningPreviewFragment -> {
                            stepIndicator.setPassedStep(3)
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
}