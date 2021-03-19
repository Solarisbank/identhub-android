package de.solarisbank.identhub.identity.summary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.BaseActivity;
import de.solarisbank.identhub.databinding.ActivitySummaryIdentityBinding;
import de.solarisbank.identhub.di.ActivityComponent;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;
import de.solarisbank.identhub.navigation.NaviDirection;
import de.solarisbank.shared.result.Event;

public final class IdentitySummaryActivity extends BaseActivity {

    private IdentitySummaryViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySummaryIdentityBinding binding = ActivitySummaryIdentityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initGraph();
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();

        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(IdentitySummaryViewModel.class);

        viewModel.getNaviDirectionEvent().observe(this, this::onNavigationChanged);

    }

    private void onNavigationChanged(@NonNull Event<NaviDirection> event) {
        NaviDirection naviDirection = event.getContent();

        if (naviDirection != null) {
            int naviActionId = naviDirection.getActionId();
            if (naviActionId == IdentityActivityViewModel.ACTION_STOP_WITH_RESULT) {
                quit(naviDirection.getArgs());
            }
        }
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

    public void initGraph() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavInflater navInflater = navHostFragment.getNavController().getNavInflater();
        NavGraph navGraph = navInflater.inflate(R.navigation.summary_nav_graph);
        navHostFragment.getNavController().setGraph(navGraph, getIntent().getExtras());
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }
}
