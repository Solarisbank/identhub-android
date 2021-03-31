package de.solarisbank.identhub.success;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.base.IdentHubFragment;
import de.solarisbank.identhub.databinding.FragmentSuccessMessageBinding;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;

public abstract class SuccessMessageFragment extends IdentHubFragment {

    protected SuccessViewModel viewModel;
    protected FragmentSuccessMessageBinding binding;
    protected IdentityActivityViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSuccessMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        sharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(IdentityActivityViewModel.class);
    }

    protected void initViews() {
        binding.title.setText(getTitle());
        binding.description.setText(getMessage());
        binding.submitButton.setText(getSubmitButtonLabel());
        binding.submitButton.setOnClickListener(view -> {
            viewModel.onSubmitButtonClicked();
        });
    }

    @StringRes
    protected abstract int getMessage();

    @StringRes
    protected abstract int getTitle();

    @StringRes
    protected abstract int getSubmitButtonLabel();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
