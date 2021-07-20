package de.solarisbank.identhub.contract

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubActivity
import de.solarisbank.identhub.di.IdentHubActivitySubcomponent
import de.solarisbank.identhub.identity.IdentityActivityViewModel
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.ui.SolarisIndicatorView
import de.solarisbank.identhub.ui.StepIndicator
import de.solarisbank.sdk.core.data.model.StateUiModel
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import timber.log.Timber

class ContractActivity : IdentHubActivity() {
    private lateinit var viewModel: ContractViewModel
    private lateinit var solarisIndicator: StepIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract)
        Timber.d("intent.getStringExtra(IdentHub.SESSION_URL_KEY): ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initGraph()
        initView()
        initViewModel()
    }

    fun initGraph() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navInflater = navHostFragment!!.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.contract_nav_graph)
        navHostFragment.navController.setGraph(navGraph, intent.extras)
    }

    private fun initView() {
        solarisIndicator = findViewById(R.id.stepIndicator)
        solarisIndicator.setStep(SolarisIndicatorView.THIRD_STEP)
        setTitle(R.string.identity_activity_third_step_label)
    }

    override fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(ContractViewModel::class.java)
        viewModel.getNaviDirectionEvent().observe(this, Observer { event: Event<NaviDirection> -> onNavigationChanged(event) })
        viewModel.getUiState().observe(this, { onIdentificationStatusChanged(it) })
    }

    private fun onIdentificationStatusChanged(model: StateUiModel<Bundle?>) {
        Timber.d("onIdentificationStatusChanged, model : ${model.data}")
        viewModel.sendResult(model.data)
    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        val naviDirection = event.content
        if (naviDirection != null) {
            viewModel.doOnNavigationChanged(naviDirection.actionId)
            when (naviDirection.actionId) {
                IdentityActivityViewModel.ACTION_QUIT, IdentityActivityViewModel.ACTION_STOP_WITH_RESULT -> {
                    Timber.d("onNavigationChanged 1")
                    quit(naviDirection.args)
                }
                else -> {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(naviDirection.actionId, naviDirection.args)
                }
            }
        }
    }
}
