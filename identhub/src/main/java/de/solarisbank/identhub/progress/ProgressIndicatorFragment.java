package de.solarisbank.identhub.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.databinding.FragmentVerificationProgressIndicatorBinding;
import de.solarisbank.identhub.BaseFragment;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;

public abstract class ProgressIndicatorFragment extends BaseFragment {

    private static final int NO_RESOURCE = -1;
    public ViewModelFactory viewModelFactory;
    protected IdentityActivityViewModel sharedViewModel;

    protected FragmentVerificationProgressIndicatorBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVerificationProgressIndicatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initViewModel();
    }

    protected void initViews() {
        int iconResource = getIconResource();
        if (iconResource != NO_RESOURCE) {
            binding.icon.setImageResource(iconResource);
        }

        binding.title.setText(getTitleResource());

        int messageResource = getMessageResource();
        if (messageResource != NO_RESOURCE) {
            binding.description.setText(messageResource);
            binding.description.setVisibility(View.VISIBLE);
        }
    }

    @StringRes
    protected int getMessageResource() {
        return NO_RESOURCE;
    }

    @StringRes
    protected abstract int getTitleResource();

    @DrawableRes
    protected int getIconResource() {
        return NO_RESOURCE;
    }

    protected void initViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(IdentityActivityViewModel.class);
    }
}
