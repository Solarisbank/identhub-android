package de.solarisbank.identhub.identity;

import android.app.Activity;
import android.content.Intent;
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
import de.solarisbank.identhub.identity.summary.IdentitySummaryActivity;
import de.solarisbank.identhub.session.IdentHubSession;
import de.solarisbank.identhub.ui.StepIndicatorView;
import de.solarisbank.sdk.core.navigation.NaviDirection;
import de.solarisbank.sdk.core.result.Event;

public final class IdentityActivity extends IdentHubActivity {

    private IdentityActivityViewModel viewModel;

    private StepIndicatorView stepIndicator;

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

        IdentHubSession.Step lastCompletedStep = viewModel.getLastCompletedStep();
        if (lastCompletedStep == IdentHubSession.Step.VERIFICATION_BANK) {
            navGraph.setStartDestination(R.id.contractSigningPreviewFragment);
        }
        navHostFragment.getNavController().setGraph(navGraph, getIntent().getExtras());
    }

    private void initView() {
        stepIndicator = findViewById(R.id.stepIndicator);
        IdentHubSession.Step lastCompletedStep = viewModel.getLastCompletedStep();
        int startStep = IdentHubSession.Step.VERIFICATION_PHONE.getIndex();
        if (!IdentHubSession.hasPhoneVerification) {
            startStep = IdentHubSession.Step.VERIFICATION_BANK.getIndex();
        }
        stepIndicator.setStep(lastCompletedStep != null ? lastCompletedStep.getIndex() : startStep);
    }

    @Override
    protected void inject(IdentHubActivitySubcomponent identHubActivitySubcomponent) {
        identHubActivitySubcomponent.inject(this);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();

        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(IdentityActivityViewModel.class);

        viewModel.getNaviDirectionEvent().observe(this, this::onNavigationChanged);

    }

    private void onNavigationChanged(@NonNull Event<NaviDirection> event) {
        NaviDirection naviDirection = event.getContent();

        if (naviDirection != null) {
            viewModel.doOnNavigationChanged(naviDirection.getActionId());
            int naviActionId = naviDirection.getActionId();

            if (naviActionId == IdentityActivityViewModel.ACTION_SUMMARY_WITH_RESULT) {
                startSummaryActivity();
            } else if (naviActionId != IdentityActivityViewModel.ACTION_QUIT &&
                    naviActionId != IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(naviActionId, naviDirection.getArgs());
            } else {
                quit(naviDirection.getArgs());
                return;
            }

            if (naviDirection.getActionId() == R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment) {
                stepIndicator.setStep(StepIndicatorView.SECOND_STEP);
            } else if (naviDirection.getActionId() == R.id.action_verificationBankSuccessMessageFragment_to_contractSigningPreviewFragment) {
                stepIndicator.setStep(StepIndicatorView.THIRD_STEP);
            }
        }
    }

    private void startSummaryActivity() {
        Intent intent = new Intent(this, IdentitySummaryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
    }

    private void quit(Bundle bundle) {
        Intent intent = null;
        if (bundle != null) {
            intent = new Intent();
            intent.putExtras(bundle);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}