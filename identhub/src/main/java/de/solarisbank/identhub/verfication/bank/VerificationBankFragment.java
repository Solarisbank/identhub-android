package de.solarisbank.identhub.verfication.bank;

import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.FragmentVerificationBankBinding;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.BaseFragment;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;

import static de.solarisbank.identhub.verfication.bank.VerificationBankViewModel.DISABLED_STATE;
import static de.solarisbank.identhub.verfication.bank.VerificationBankViewModel.ERROR_STATE;

public class VerificationBankFragment extends BaseFragment {

    ViewModelFactory viewModelFactory;

    private VerificationBankViewModel viewModel;

    private IdentityActivityViewModel sharedViewModel;

    private FragmentVerificationBankBinding binding;

    public static Fragment newInstance() {
        return new VerificationBankFragment();
    }

    @Override
    protected void inject(LibraryComponent component) {
        component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVerificationBankBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initViews();
        observeInputsState();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(VerificationBankViewModel.class);

        sharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(IdentityActivityViewModel.class);
    }

    private void initViews() {
        binding.submitButton.setOnClickListener(view -> viewModel.onSubmitButtonClicked());
    }

    private void observeInputsState() {
        viewModel.getState().observe(getViewLifecycleOwner(), this::onStateInputChanged);
    }

    private void onStateInputChanged(Integer value) {
        int state = value;
        ((LevelListDrawable) binding.ibanNumber.getBackground()).setLevel(state);
        binding.errorMessage.setVisibility(state == ERROR_STATE ? View.VISIBLE : View.GONE);

        if (value == DISABLED_STATE) {
            sharedViewModel.navigateTo(R.id.action_verificationBankFragment_to_establishConnectionFragment);
        }
    }
}

