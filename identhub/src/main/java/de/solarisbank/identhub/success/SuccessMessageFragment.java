package de.solarisbank.identhub.success;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.IdentHubFragment;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;

public abstract class SuccessMessageFragment extends IdentHubFragment {

    protected SuccessViewModel viewModel;
    protected IdentityActivityViewModel sharedViewModel;

    protected TextView title;
    protected TextView description;
    protected TextView submitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_success_message, container, false);
        title = root.findViewById(R.id.title);
        description = root.findViewById(R.id.description);
        submitButton = root.findViewById(R.id.submitButton);
        return root;
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
        title.setText(getTitle());
        description.setText(getMessage());
        submitButton.setText(getSubmitButtonLabel());
        submitButton.setOnClickListener(view -> {
            viewModel.onSubmitButtonClicked();
        });
    }

    @StringRes
    protected abstract int getMessage();

    @StringRes
    protected abstract int getTitle();

    @StringRes
    protected abstract int getSubmitButtonLabel();

}
