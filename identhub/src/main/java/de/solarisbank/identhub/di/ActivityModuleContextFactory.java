package de.solarisbank.identhub.di;

import android.content.Context;

import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Preconditions;

public final class ActivityModuleContextFactory implements Factory<Context> {

    private final ActivityModule activityModule;

    public ActivityModuleContextFactory(ActivityModule activityModule) {
        this.activityModule = activityModule;
    }

    public static ActivityModuleContextFactory create(ActivityModule activityModule) {
        return new ActivityModuleContextFactory(activityModule);
    }

    @Override
    public Context get() {
        return Preconditions.checkNotNull(
                activityModule.provideContext(),
                "Cannot return null from provider method"
        );
    }
}
