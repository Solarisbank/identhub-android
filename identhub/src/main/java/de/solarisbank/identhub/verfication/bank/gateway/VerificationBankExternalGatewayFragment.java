package de.solarisbank.identhub.verfication.bank.gateway;

import android.content.res.Configuration;
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
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.IdentHubFragment;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.result.Event;
import de.solarisbank.sdk.core.result.Result;
import timber.log.Timber;

public final class VerificationBankExternalGatewayFragment extends IdentHubFragment {

    private VerificationBankViewModel sharedViewModel;
    private VerificationBankExternalGateViewModel verificationBankExternalGateViewModel;
    private WebView webView;

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
        View root = inflater.inflate(R.layout.fragment_verification_bank_external_gateway, container, false);
        webView = root.findViewById(R.id.webView);
        return root;
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
                .get(VerificationBankViewModel.class);

        verificationBankExternalGateViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(VerificationBankExternalGateViewModel.class);
    }

    private void initWebView() {
        Event<String> bankVerificationUrlEvent = verificationBankExternalGateViewModel.getVerificationBankUrl().getValue();
        String verificationBankUrl = bankVerificationUrlEvent.getContent();

        Preconditions.checkNotNull(verificationBankUrl);

        webView.getSettings().setJavaScriptEnabled(true);
        enableWebViewDarkModeSupport();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webView.loadUrl(verificationBankUrl);
    }

    private void enableWebViewDarkModeSupport() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(webView.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                }
                WebSettingsCompat.setForceDarkStrategy(webView.getSettings(), WebSettingsCompat.DARK_STRATEGY_WEB_THEME_DARKENING_ONLY);
            }
        }
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
}

