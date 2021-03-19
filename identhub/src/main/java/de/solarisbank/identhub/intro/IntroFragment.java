package de.solarisbank.identhub.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.BaseFragment;
import de.solarisbank.identhub.databinding.FragmentOnboardingIntroBinding;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.identity.IdentityActivity;

public final class IntroFragment extends BaseFragment {
    public static final String TAG = "IntroFragment";

    private FragmentOnboardingIntroBinding binding;

    public static Fragment newInstance() {
        return new IntroFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingIntroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        binding.phoneNumber.setText(String.format(getString(R.string.onboarding_intro_phone_number), ""));
        binding.startVerifyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), IdentityActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            getActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }
}
