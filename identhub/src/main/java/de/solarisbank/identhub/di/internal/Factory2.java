package de.solarisbank.identhub.di.internal;

public interface Factory2<T, R> {

    T create(R value);
}
