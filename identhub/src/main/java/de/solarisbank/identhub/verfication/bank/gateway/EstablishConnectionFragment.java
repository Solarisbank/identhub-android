package de.solarisbank.identhub.verfication.bank.gateway;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.progress.ProgressIndicatorFragment;
import de.solarisbank.shared.result.Event;

public final class EstablishConnectionFragment extends ProgressIndicatorFragment {

    private VerificationBankExternalGateViewModel verificationBankExternalGateViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeScreenState();
    }

    private void observeScreenState() {
        verificationBankExternalGateViewModel.getEstablishSecureConnectionEvent().observe(getViewLifecycleOwner(), this::onSecureConnectionEstablished);
    }

    private void onSecureConnectionEstablished(Event<Object> event) {
        sharedViewModel.moveToExternalGate();
    }

    @Override
    protected int getIconResource() {
        return R.drawable.ic_shield_32_secondary;
    }

    @Override
    protected int getTitleResource() {
        return R.string.progress_indicator_secure_connection_message;
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        verificationBankExternalGateViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(VerificationBankExternalGateViewModel.class);
    }
}