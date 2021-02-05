package de.solarisbank.identhub.contract.preview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.FragmentContractSigningPreviewBinding;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.BaseFragment;
import de.solarisbank.identhub.contract.adapter.DocumentAdapter;
import de.solarisbank.identhub.identity.IdentityActivityViewModel;

public class ContractSigningPreviewFragment extends BaseFragment {
    private final DocumentAdapter adapter = new DocumentAdapter();
    public ViewModelFactory viewModelFactory;

    private FragmentContractSigningPreviewBinding binding;
    private IdentityActivityViewModel sharedViewModel;

    public static Fragment newInstance() {
        return new ContractSigningPreviewFragment();
    }

    @Override
    protected void inject(LibraryComponent component) {
        component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContractSigningPreviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initViews();
    }

    private void initViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(IdentityActivityViewModel.class);
    }

    private void initViews() {
        binding.documentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.documentsList.setHasFixedSize(true);
        binding.documentsList.setAdapter(adapter);
        binding.submitButton.setOnClickListener(view -> sharedViewModel.navigateTo(R.id.action_contractSigningPreviewFragment_to_contractSigningFragment));
    }
}
