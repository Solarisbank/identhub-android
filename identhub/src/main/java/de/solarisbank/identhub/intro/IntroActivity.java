package de.solarisbank.identhub.intro;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import de.solarisbank.identhub.ViewModelFactory;
import de.solarisbank.identhub.R;
import de.solarisbank.identhub.databinding.ActivityIntroBinding;
import de.solarisbank.identhub.di.LibraryComponent;

public class IntroActivity extends AppCompatActivity {

    protected ViewModelFactory viewModelFactory;

    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LibraryComponent.getInstance().inject(this);
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        IntroActivityViewModel viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(IntroActivityViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, IntroFragment.newInstance(), IntroFragment.TAG).commit();
        }
    }
}