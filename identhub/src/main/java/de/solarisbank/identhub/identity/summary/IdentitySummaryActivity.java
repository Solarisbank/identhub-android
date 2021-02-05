package de.solarisbank.identhub.identity.summary;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.ActivitySummaryIdentityBinding;
import de.solarisbank.identhub.di.LibraryComponent;

public class IdentitySummaryActivity extends AppCompatActivity {
    protected ViewModelFactory viewModelFactory;

    private ActivitySummaryIdentityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LibraryComponent.getInstance().inject(this);
        super.onCreate(savedInstanceState);
        binding = ActivitySummaryIdentityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, IdentitySummaryFragment.newInstance(), IdentitySummaryFragment.TAG).commit();
        }
    }
}
