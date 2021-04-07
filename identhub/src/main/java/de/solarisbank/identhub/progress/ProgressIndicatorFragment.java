package de.solarisbank.identhub.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.IdentHubFragment;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;

public abstract class ProgressIndicatorFragment extends IdentHubFragment {

    private static final int NO_RESOURCE = -1;

    protected IdentityActivityViewModel sharedViewModel;

    protected ImageView icon;
    protected TextView title;
    protected TextView description;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_verification_progress_indicator, container, false);
        icon = root.findViewById(R.id.icon);
        title = root.findViewById(R.id.title);
        description = root.findViewById(R.id.description);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    protected void initViews() {
        int iconResource = getIconResource();
        if (iconResource != NO_RESOURCE) {
            icon.setImageResource(iconResource);
        }

        title.setText(getTitleResource());

        int messageResource = getMessageResource();
        if (messageResource != NO_RESOURCE) {
            description.setText(messageResource);
            description.setVisibility(View.VISIBLE);
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

    @Override
    protected void initViewModel() {
        super.initViewModel();
        sharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(IdentityActivityViewModel.class);
    }
}
