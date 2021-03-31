package de.solarisbank.identhub.verfication.bank.gateway;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.base.IdentHubFragment;
import de.solarisbank.identhub.databinding.FragmentVerificationBankExternalGatewayBinding;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.result.Event;
import de.solarisbank.sdk.core.result.Result;
import timber.log.Timber;

public final class VerificationBankExternalGatewayFragment extends IdentHubFragment {

    private IdentityActivityViewModel sharedViewModel;
    private VerificationBankExternalGateViewModel verificationBankExternalGateViewModel;
    private FragmentVerificationBankExternalGatewayBinding binding;

    public static Fragment newInstance() {
        return new VerificationBankExternalGatewayFragment();
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVerificationBankExternalGatewayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWebView();
        observeVerificationStatus();
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        sharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(IdentityActivityViewModel.class);

        verificationBankExternalGateViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(VerificationBankExternalGateViewModel.class);
    }

    private void initWebView() {
        Event<String> bankVerificationUrlEvent = verificationBankExternalGateViewModel.getVerificationBankUrl().getValue();
        String verificationBankUrl = bankVerificationUrlEvent.getContent();

        Preconditions.checkNotNull(verificationBankUrl);

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        binding.webView.loadUrl(verificationBankUrl);
    }

    private void observeVerificationStatus() {
        verificationBankExternalGateViewModel.getVerificationResultLiveData().observe(getViewLifecycleOwner(), this::onVerificationStatusChanged);
    }

    private void onVerificationStatusChanged(Result<Object> result) {
        if (result instanceof Result.Success) {
            sharedViewModel.navigateToProcessingVerification();
        } else if (result instanceof Result.Error) {
            Timber.d("Could not find verification result");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

