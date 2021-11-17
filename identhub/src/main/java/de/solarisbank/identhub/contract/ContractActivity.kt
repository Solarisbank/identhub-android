package de.solarisbank.identhub.contract

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubActivity
import de.solarisbank.identhub.data.dto.QesStepParametersDto
import de.solarisbank.identhub.di.IdentHubActivitySubcomponent
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.identhub.session.feature.navigation.router.IS_FOURTHLINE_SIGNING
import de.solarisbank.identhub.session.feature.navigation.router.SHOW_STEP_INDICATOR
import de.solarisbank.sdk.domain.model.result.Event
import de.solarisbank.sdk.feature.view.ConstraintStepIndicator
import timber.log.Timber

class ContractActivity : IdentHubActivity() {
    private lateinit var viewModel: ContractViewModel
    private lateinit var solarisIndicator: ConstraintStepIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract)
        Timber.d("intent.getStringExtra(IdentHub.SESSION_URL_KEY): ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initGraph()
        initView()
        observeViewModel()
        viewModel.saveQesStepParameters(intent.toQesStepParameters())
    }

    private fun Intent.toQesStepParameters(): QesStepParametersDto {
        return QesStepParametersDto(
            isFourthlineSigning = getBooleanExtra(IS_FOURTHLINE_SIGNING, false),
            showStepIndicator = this.getBooleanExtra(SHOW_STEP_INDICATOR, true)
        )
    }

    fun initGraph() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navInflater = navHostFragment!!.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.contract_nav_graph)
        navHostFragment.navController.setGraph(navGraph, intent.extras)
    }

    private fun initView() {
        solarisIndicator = findViewById(R.id.stepIndicator)
        if (intent.getBooleanExtra(SHOW_STEP_INDICATOR, true)) {
            solarisIndicator.visibility = View.VISIBLE
        } else {
            solarisIndicator.visibility = View.GONE
        }
        solarisIndicator.setCurrentStepLabel("Sign documents") //todo create ticket for translation
        solarisIndicator.setPassedStep(3)
        setTitle(R.string.identity_activity_third_step_label)
    }

    override fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this)
    }

    private fun observeViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(ContractViewModel::class.java)
        viewModel.getNaviDirectionEvent().observe(
            this, Observer
            { event: Event<NaviDirection> -> onNavigationChanged(event) }
        )
    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        val naviDirection = event.content
        Timber.d("onNavigationChanged 0, naviDirection: ${naviDirection}")
        if (naviDirection != null) {
            when (naviDirection) {
                is SessionStepResult -> {
                    Timber.d("onNavigationChanged 1")
                    quit(naviDirection)
                }
                is NaviDirection.FragmentDirection -> {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(naviDirection.actionId, naviDirection.args)
                }
            }
        }
    }
}
