package de.solarisbank.identhub.di.internal;

public interface MembersInjector<T> {
    void injectMembers(T instance);
}
