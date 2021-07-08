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
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.router.COMPLETED_STEP
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.identhub.ui.SolarisIndicatorView
import de.solarisbank.identhub.ui.StepIndicator
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import timber.log.Timber

class VerificationBankActivity : IdentHubActivity() {
    var iban: String? = null
    private lateinit var viewModel: VerificationBankViewModel
    private lateinit var stepIndicator: StepIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_bank)
        Timber.d("intent.getStringExtra(IdentHub.SESSION_URL_KEY): ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initGraph()
    }

    private fun initGraph() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navInflater = navHostFragment!!.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.bank_nav_graph)
        navHostFragment.navController.setGraph(navGraph, intent.extras)
        initView()
    }

    private fun initView() {
        stepIndicator = findViewById(R.id.stepIndicator)
        val lastCompletedStep = viewModel.getLastCompletedStep()
        var startStep = COMPLETED_STEP.VERIFICATION_PHONE.index
        if (!IdentHubSession.hasPhoneVerification) {
            startStep = COMPLETED_STEP.VERIFICATION_BANK.index
        }
        stepIndicator.setStep(lastCompletedStep?.index ?: startStep)
    }

    override fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this)
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(VerificationBankViewModel::class.java)
        viewModel.getNaviDirectionEvent().observe(this, Observer { event: Event<NaviDirection> -> onNavigationChanged(event) })
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
        if (naviDirection.actionId == R.id.action_verificationBankIntroFragment_to_verificationBankIbanFragment) {
            stepIndicator.setStep(SolarisIndicatorView.SECOND_STEP)
        } else if (naviDirection.actionId == R.id.action_processingVerificationFragment_to_contractSigningPreviewFragment) {
            stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
        }

        when (naviDirection.actionId) {
            R.id.action_verificationBankFragment_to_establishConnectionFragment,
            R.id.action_establishConnectionFragment_to_verificationBankExternalGatewayFragment,
            R.id.action_verificationBankExternalGatewayFragment_to_processingVerificationFragment
            -> {
                stepIndicator.visibility = View.GONE
            }
            else -> stepIndicator.visibility = View.VISIBLE
        }
    }
}