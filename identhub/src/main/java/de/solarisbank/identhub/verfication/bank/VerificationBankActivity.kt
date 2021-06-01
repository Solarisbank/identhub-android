package de.solarisbank.identhub.verfication.bank

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubActivity
import de.solarisbank.identhub.contract.ContractActivity
import de.solarisbank.identhub.di.IdentHubActivitySubcomponent
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.router.NEXT_STEP_KEY
import de.solarisbank.identhub.router.toNextStep
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.identhub.ui.StepIndicatorView
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import timber.log.Timber

class VerificationBankActivity : IdentHubActivity() {
    private lateinit var viewModel: VerificationBankViewModel
    private lateinit var stepIndicator: StepIndicatorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_bank)
        Timber.d("intent.getStringExtra(IdentHub.SESSION_URL_KEY): ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initGraph()
    }

    private fun initGraph() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navInflater = navHostFragment!!.navController.navInflater
        val lastCompletedStep = viewModel.getLastCompletedStep()
        if (lastCompletedStep === IdentHubSession.Step.VERIFICATION_BANK) {
            startContractSigningActivity()
            return
        } else {
            val navGraph = navInflater.inflate(R.navigation.bank_nav_graph)
            navHostFragment.navController.setGraph(navGraph, intent.extras)
            initView()
        }
    }

    private fun initView() {
        stepIndicator = findViewById(R.id.stepIndicator)
        val lastCompletedStep = viewModel.getLastCompletedStep()
        var startStep = IdentHubSession.Step.VERIFICATION_PHONE.index
        if (!IdentHubSession.hasPhoneVerification) {
            startStep = IdentHubSession.Step.VERIFICATION_BANK.index
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

        if (naviDirection != null) {
            viewModel.doOnNavigationChanged(naviDirection.actionId)
            val naviActionId = naviDirection.actionId
            if (naviActionId == IdentHubSession.ACTION_NEXT_STEP) {
                forwardTo(naviDirection.args!!)
            } else if (naviActionId != IdentityActivityViewModel.ACTION_QUIT && //todo refactor
                    naviActionId != IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(naviActionId, naviDirection.args)
            } else if (naviActionId != IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
                quit(naviDirection.args)
                return
            }  else {
                quit(naviDirection.args)
                return
            }
            if (naviDirection.actionId == R.id.action_verificationBankIntroFragment_to_verificationBankIbanFragment) {
                stepIndicator.setStep(StepIndicatorView.SECOND_STEP)
            } else if (naviDirection.actionId == R.id.action_verificationBankSuccessMessageFragment_to_contractSigningPreviewFragment) {
                stepIndicator.setStep(StepIndicatorView.THIRD_STEP)
            }
        }
    }

    //todo
    private fun startContractSigningActivity() {
        startActivity(Intent(this, ContractActivity::class.java))
        finish()
    }

    fun forwardTo(args: Bundle) {
        val nextStep = args.getString(NEXT_STEP_KEY)
        val forwardIntent = toNextStep(this, nextStep!!)
        forwardIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        forwardIntent.putExtras(args)
        forwardIntent.putExtra(IdentHub.SESSION_URL_KEY, intent.getStringExtra(IdentHub.SESSION_URL_KEY))
        startActivity(forwardIntent)
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
}