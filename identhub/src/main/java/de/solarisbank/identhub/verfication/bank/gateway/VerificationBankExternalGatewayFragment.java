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

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.FragmentVerificationBankExternalGatewayBinding;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.BaseFragment;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;

public class VerificationBankExternalGatewayFragment extends BaseFragment {

    ViewModelFactory viewModelFactory;

    private IdentityActivityViewModel sharedViewModel;
    private FragmentVerificationBankExternalGatewayBinding binding;

    public static Fragment newInstance() {
        return new VerificationBankExternalGatewayFragment();
    }

    @Override
    protected void inject(LibraryComponent component) {
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
        initViewModel();
        binding.webView.loadUrl("https://www.google.pl");
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        binding.emptyButton.setOnClickListener(
                button -> sharedViewModel.navigateTo(R.id.action_verificationBankExternalGatewayFragment_to_processingVerificationFragment)
        );
    }

    private void initViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(IdentityActivityViewModel.class);
    }
}

