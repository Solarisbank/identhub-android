package de.solarisbank.identhub;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.solarisbank.identhub.di.LibraryComponent;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        inject(LibraryComponent.getInstance());
        super.onCreate(savedInstanceState);
    }

    protected abstract void inject(LibraryComponent component);
}
