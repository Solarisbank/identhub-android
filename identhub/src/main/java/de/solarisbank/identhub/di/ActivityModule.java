package de.solarisbank.identhub.di;

import android.content.Context;
import android.content.SharedPreferences;

import de.solarisbank.identhub.base.BaseActivity;
import de.solarisbank.identhub.data.preferences.IdentificationStepPreferences;

import static android.content.Context.MODE_PRIVATE;

public final class ActivityModule {

    private final BaseActivity baseActivity;

    public ActivityModule(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public Context provideContext() {
        return baseActivity;
    }

    public SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences("identhub", MODE_PRIVATE);
    }

    public IdentificationStepPreferences provideIdentificationStepPreferences(SharedPreferences sharedPreferences) {
        return new IdentificationStepPreferences(sharedPreferences);
    }
}
