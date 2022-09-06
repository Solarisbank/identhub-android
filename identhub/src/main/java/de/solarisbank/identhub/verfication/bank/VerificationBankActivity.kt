package de.solarisbank.identhub.verfication.bank

import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubActivity
import de.solarisbank.identhub.di.IdentHubActivitySubcomponent
import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.identhub.session.feature.navigation.NaviDirection
import de.solarisbank.identhub.session.feature.navigation.SessionStepResult
import de.solarisbank.sdk.domain.model.result.Event
import timber.log.Timber

class VerificationBankActivity : IdentHubActivity() {
    private lateinit var viewModel: VerificationBankViewModel

    private var bankVerificationClose: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.identhub_activity_verification_bank)
        bankVerificationClose = findViewById(R.id.img_bank_verification_close)
        observeViewModel()
        Timber.d("intent.getStringExtra(IdentHub.SESSION_URL_KEY): ${intent.getStringExtra(IdentHub.SESSION_URL_KEY)}")
        initGraph()

        bankVerificationClose?.setOnClickListener {
           onBackPressed()
        }
    }

    private fun initGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navInflater = navHostFragment!!.navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.identhub_bank_nav_graph)
        navGraph.setStartDestination(
            if (viewModel.isPhoneVerified()) R.id.verificationBankIbanFragment
            else R.id.phoneVerificationFragment
        )
        navHostFragment.navController
            .setGraph(navGraph, intent.extras)
    }

    override fun inject(identHubActivitySubcomponent: IdentHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this)
    }

    private fun observeViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(VerificationBankViewModel::class.java)
        viewModel.getNaviDirectionEvent().observe(this
        ) { event: Event<NaviDirection> -> onNavigationChanged(event) }
        viewModel.cancelState.observe(this) { onCancel(it) }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume() $this")
    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        val naviDirection = event.content
        Timber.d("onNavigationChanged, naviDirection : $naviDirection")

        if (naviDirection != null) {
            when (naviDirection) {
                is NaviDirection.FragmentDirection -> {
                    val naviActionId = naviDirection.actionId
                    Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(naviActionId, naviDirection.args)
                }
                is SessionStepResult -> {
                    Timber.d("onNavigationChanged 2; naviDirection: ${naviDirection}")
                    quit(naviDirection)
                }
            }
        }
    }

    private fun onCancel(state: Boolean?) {
        if(state == true) {
            quit(NaviDirection.VerificationFailureStepResult())
        }
    }
}