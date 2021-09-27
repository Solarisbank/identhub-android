package de.solarisbank.sdk.feature.di.internal;

public interface Factory2<T, R> {

    T create(R value);
}
