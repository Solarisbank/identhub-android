package de.solarisbank.identhub.contract

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
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
import de.solarisbank.sdk.logger.IdLogger
import timber.log.Timber

class ContractActivity : IdentHubActivity() {
    private lateinit var viewModel: ContractViewModel
    private var contractClose: AppCompatImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.identhub_activity_contract)
        contractClose = findViewById(R.id.img_contract_close)
        Timber.d("intent.getStringExtra(IdentHub.SESSION_URL_KEY): ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initGraph()
        observeViewModel()
        viewModel.saveQesStepParameters(intent.toQesStepParameters())

        contractClose?.setOnClickListener {
            onBackPressed()
        }
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
        val navGraph = navInflater.inflate(R.navigation.identhub_contract_nav_graph)
        navHostFragment.navController.setGraph(navGraph, intent.extras)
    }


    override fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this)
    }

    private fun observeViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(ContractViewModel::class.java)
        viewModel.getNaviDirectionEvent().observe(this)
        { event: Event<NaviDirection> -> onNavigationChanged(event) }
    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        val naviDirection = event.content
        Timber.d("onNavigationChanged 0, naviDirection: $naviDirection")
        IdLogger.logNav("onNavigationChanged 0, naviDirection: $naviDirection")
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
