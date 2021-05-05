package de.solarisbank.sdk.fourthline

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavInflater
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import de.solarisbank.sdk.core.navigation.NaviDirection
import de.solarisbank.sdk.core.result.Event
import de.solarisbank.sdk.fourthline.di.FourthlineActivitySubcomponent

class FourthlineActivity : FourthlineBaseActivity() {

    private lateinit var viewModel: FourthlineViewModel

    private lateinit var navHostFragment: View

    override fun inject(activitySubcomponent: FourthlineActivitySubcomponent) {
        activitySubcomponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourthline)
        initViewModel()
        initGraph()
        initView()
    }

    private fun initView() {
        navHostFragment = findViewById(R.id.nav_host_fragment)
//initView        val lastCompletedStep: IdentHubSession.Step = viewModel.getLastCompletedStep()
//        binding.stepIndicator.setStep(lastCompletedStep?.index
//                ?: IdentHubSession.Step.VERIFICATION_PHONE.index)
    }

    private fun initGraph() {
        val navController = (
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment
                ) as NavHostFragment).navController
        val navInflater: NavInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.fourthline_navigation)
        navGraph.startDestination = R.id.termsAndConditionsFragment
        navController.graph = navGraph
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(FourthlineViewModel::class.java)

        viewModel.navigationActionId.observe(this,  Observer { event -> onNavigationChanged(event) })
    }

    private fun onNavigationChanged(event: Event<NaviDirection>) {
        Navigation.findNavController(navHostFragment).navigate(event.content?.actionId!!, event.content?.args)
    }

}