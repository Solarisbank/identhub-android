package de.solarisbank.identhub.identity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.IdentHubActivity;
import de.solarisbank.identhub.di.IdentHubActivitySubcomponent;
import de.solarisbank.identhub.router.COMPLETED_STEP;
import de.solarisbank.identhub.session.IdentHubSession;
import de.solarisbank.identhub.ui.SolarisIndicatorView;
import de.solarisbank.sdk.core.navigation.NaviDirection;
import de.solarisbank.sdk.core.result.Event;

public final class IdentityActivity extends IdentHubActivity {

    private IdentityActivityViewModel viewModel;

    private SolarisIndicatorView stepIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);
        initGraph();
        initView();
    }

    public void initGraph() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavInflater navInflater = navHostFragment.getNavController().getNavInflater();
        NavGraph navGraph = navInflater.inflate(R.navigation.identity_nav_graph);

        if (!IdentHubSession.hasPhoneVerification) {
            navGraph.setStartDestination(R.id.verificationBankFragment);
        }

        COMPLETED_STEP lastCompletedStep = viewModel.getLastCompletedStep();
        if (lastCompletedStep == COMPLETED_STEP.VERIFICATION_BANK) {
            navGraph.setStartDestination(R.id.contractSigningPreviewFragment);
        }
        navHostFragment.getNavController().setGraph(navGraph, getIntent().getExtras());
    }

    private void initView() {
        stepIndicator = findViewById(R.id.stepIndicator);
        COMPLETED_STEP lastCompletedStep = viewModel.getLastCompletedStep();
        int startStep = COMPLETED_STEP.VERIFICATION_PHONE.getIndex();
        if (!IdentHubSession.hasPhoneVerification) {
            startStep = COMPLETED_STEP.VERIFICATION_BANK.getIndex();
        }
        stepIndicator.setStep(lastCompletedStep != null ? lastCompletedStep.getIndex() : startStep);
    }

    @Override
    protected void inject(IdentHubActivitySubcomponent identHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(IdentityActivityViewModel.class);

        viewModel.getNaviDirectionEvent().observe(this, this::onNavigationChanged);

    }

    private void onNavigationChanged(@NonNull Event<NaviDirection> event) {
        NaviDirection naviDirection = event.getContent();

        if (naviDirection != null) {
            viewModel.doOnNavigationChanged(naviDirection.getActionId());
            int naviActionId = naviDirection.getActionId();

            if (naviActionId != IdentityActivityViewModel.ACTION_QUIT &&
                    naviActionId != IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(naviActionId, naviDirection.getArgs());
            } else {
                quit(naviDirection.getArgs());
                return;
            }

            if (naviDirection.getActionId() == R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment) {
                stepIndicator.setStep(SolarisIndicatorView.SECOND_STEP);
            } else if (naviDirection.getActionId() == R.id.action_processingVerificationFragment_to_contractSigningPreviewFragment) {
                stepIndicator.setStep(SolarisIndicatorView.THIRD_STEP);
            }
        }
    }
}