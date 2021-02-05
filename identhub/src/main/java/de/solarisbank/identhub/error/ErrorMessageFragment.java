package de.solarisbank.identhub.error;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import de.solarisbank.identhub.databinding.FragmentErrorMessageBinding;
import de.solarisbank.identhub.BaseFragment;

public abstract class ErrorMessageFragment extends BaseFragment {

    protected ErrorViewModel viewModel;
    private FragmentErrorMessageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentErrorMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initViews();
    }

    protected abstract void initViewModel();

    private void initViews() {
        if (viewModel == null) {
            throw new IllegalStateException("ViewModel need to be initialized");
        }

        binding.title.setText(getTitle());
        binding.description.setText(getMessage());
        binding.cancelButton.setText(getCancelButtonLabel());
        binding.cancelButton.setOnClickListener(view -> {
            viewModel.onCancelButtonClicked();
        });
        binding.submitButton.setText(getSubmitButtonLabel());
        binding.submitButton.setOnClickListener(view -> {
            viewModel.onSubmitButtonClicked();
        });
    }

    @StringRes
    protected abstract int getCancelButtonLabel();

    @StringRes
    protected abstract int getMessage();

    @StringRes
    protected abstract int getTitle();

    @StringRes
    protected abstract int getSubmitButtonLabel();
}
