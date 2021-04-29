package de.solarisbank.identhub.contract

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubActivity
import de.solarisbank.identhub.di.IdentHubActivitySubcomponent
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity
import de.solarisbank.identhub.session.IdentHubSession
import de.solarisbank.identhub.ui.StepIndicatorView
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event

class ContractActivity : IdentHubActivity() {
    private lateinit var viewModel: ContractViewModel
    private lateinit var stepIndicator: StepIndicatorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract)
        initGraph()
        initView()
    }

    fun initGraph() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navInflater = navHostFragment!!.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.contract_nav_graph)
        navHostFragment.navController.setGraph(navGraph, intent.extras)
    }

    private fun initView() {
        stepIndicator = findViewById(R.id.stepIndicator)
        val lastCompletedStep = viewModel.getLastCompletedStep()
        stepIndicator.setStep(lastCompletedStep?.index ?: IdentHubSession.Step.VERIFICATION_PHONE.index)
    }

    override fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this)
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(ContractViewModel::class.java)
        viewModel.getNaviDirectionEvent().observe(this, Observer { event: Event<NaviDirection> -> onNavigationChanged(event) })
    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        val naviDirection = event.content
        if (naviDirection != null) {
            viewModel.doOnNavigationChanged(naviDirection.actionId)
            val naviActionId = naviDirection.actionId
            if (naviActionId == IdentityActivityViewModel.ACTION_SUMMARY_WITH_RESULT) {
                startSummaryActivity()
            } else if (naviActionId != IdentityActivityViewModel.ACTION_QUIT &&
                    naviActionId != IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(naviActionId, naviDirection.args)
            } else {
                quit(naviDirection.args)
                return
            }
            if (naviDirection.actionId == R.id.action_verificationBankSuccessMessageFragment_to_contractSigningPreviewFragment) {
                stepIndicator.setStep(StepIndicatorView.THIRD_STEP)
            }
        }
    }

    private fun startSummaryActivity() {
        val intent = Intent(this, IdentitySummaryActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        startActivity(intent)
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
