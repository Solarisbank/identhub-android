package de.solarisbank.identhub.identity.summary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import de.solarisbank.identhub.databinding.FragmentIdentitySummaryBinding;
import de.solarisbank.identhub.di.LibraryComponent;
import de.solarisbank.identhub.BaseFragment;
import de.solarisbank.identhub.identity.summary.adapter.SignedDocumentAdapter;

public class IdentitySummaryFragment extends BaseFragment {
    public static final String TAG = "IdentitySummaryFragment";
    private final SignedDocumentAdapter adapter = new SignedDocumentAdapter();
    private FragmentIdentitySummaryBinding binding;

    public static Fragment newInstance() {
        return new IdentitySummaryFragment();
    }

    @Override
    protected void inject(LibraryComponent component) {
        component.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIdentitySummaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        binding.documentsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.documentsList.setHasFixedSize(true);
        binding.documentsList.setAdapter(adapter);
        binding.submitButton.setOnClickListener(v -> getActivity().finish());
    }


}
