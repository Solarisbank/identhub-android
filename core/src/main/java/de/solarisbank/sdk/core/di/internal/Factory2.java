package de.solarisbank.sdk.core.di.internal;

public interface Factory2<T, R> {

    T create(R value);
}
