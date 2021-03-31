package de.solarisbank.sdk.core.di.internal;

public interface MembersInjector<T> {
    void injectMembers(T instance);
}
