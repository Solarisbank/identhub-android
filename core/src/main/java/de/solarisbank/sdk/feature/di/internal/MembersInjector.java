package de.solarisbank.sdk.feature.di.internal;

public interface MembersInjector<T> {
    void injectMembers(T instance);
}
