package de.solarisbank.identhub.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.solarisbank.identhub.R;
import de.solarisbank.identhub.base.IdentHubFragment;
import de.solarisbank.identhub.di.FragmentComponent;
import de.solarisbank.identhub.identity.IdentityActivity;

public final class IntroFragment extends IdentHubFragment {
    public static final String TAG = "IntroFragment";

    public static Fragment newInstance() {
        return new IntroFragment();
    }

    private Button startVerifyButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_verification_bank_intro, container, false);
        startVerifyButton = root.findViewById(R.id.startVerifyButton);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        startVerifyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), IdentityActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            getActivity().finish();
        });
    }

    @Override
    protected void inject(FragmentComponent component) {
        component.inject(this);
    }
}
