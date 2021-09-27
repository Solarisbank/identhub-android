package de.solarisbank.sdk.data.di.network;

import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public final class NetworkModuleProvideRetrofitFactory implements Factory<Retrofit> {

    private final NetworkModule networkModule;

    private final Provider<MoshiConverterFactory> moshiConverterFactoryProvider;
    private final Provider<OkHttpClient> okHttpClientProvider;
    private final Provider<CallAdapter.Factory> rxJavaCallAdapterFactoryProvider;

    public NetworkModuleProvideRetrofitFactory(
            NetworkModule networkModule,
            Provider<MoshiConverterFactory> moshiConverterFactoryProvider,
            Provider<OkHttpClient> okHttpClientProvider,
            Provider<CallAdapter.Factory> rxJavaCallAdapterFactoryProvider) {
        this.networkModule = networkModule;
        this.moshiConverterFactoryProvider = moshiConverterFactoryProvider;
        this.okHttpClientProvider = okHttpClientProvider;
        this.rxJavaCallAdapterFactoryProvider = rxJavaCallAdapterFactoryProvider;
    }

    public static NetworkModuleProvideRetrofitFactory create(
            NetworkModule networkModule,
            Provider<MoshiConverterFactory> moshiConverterFactoryProvider,
            Provider<OkHttpClient> okHttpClientProvider,
            Provider<CallAdapter.Factory> rxJavaCallAdapterFactoryProvider
    ) {
        return new NetworkModuleProvideRetrofitFactory(
                networkModule,
                moshiConverterFactoryProvider,
                okHttpClientProvider,
                rxJavaCallAdapterFactoryProvider
        );
    }

    @Override
    public Retrofit get() {
        return Preconditions.checkNotNull(
                networkModule.provideRetrofit(
                        moshiConverterFactoryProvider.get(),
                        okHttpClientProvider.get(),
                        rxJavaCallAdapterFactoryProvider.get()
                ),
                "Cannot return null from provider method"
        );
    }
}
