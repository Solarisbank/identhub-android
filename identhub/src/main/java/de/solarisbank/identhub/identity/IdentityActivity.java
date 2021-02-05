package de.solarisbank.identhub.identity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.ActivityIdentityBinding;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.ui.StepIndicatorView;

public class IdentityActivity extends AppCompatActivity {

    protected ViewModelFactory viewModelFactory;

    private ActivityIdentityBinding binding;

    private IdentityActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LibraryComponent.getInstance().inject(this);
        super.onCreate(savedInstanceState);
        binding = ActivityIdentityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.stepIndicator.setStep(StepIndicatorView.FIRST_STEP);
        initViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(IdentityActivityViewModel.class);

        viewModel.getNavigationActionId().observe(this, this::onNavigationChanged);

    }

    private void onNavigationChanged(Integer action) {
        Navigation.findNavController(binding.navHostFragment).navigate(action);

        if (action == R.id.action_verificationPhoneSuccessMessageFragment_to_verificationBankFragment) {
            binding.stepIndicator.setStep(StepIndicatorView.SECOND_STEP);
        } else if (action == R.id.action_verificationBankSuccessMessageFragment_to_contractSigningPreviewFragment) {
            binding.stepIndicator.setStep(StepIndicatorView.THIRD_STEP);
        }
    }
}