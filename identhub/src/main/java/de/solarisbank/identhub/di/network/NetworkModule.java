package de.solarisbank.identhub.di.network;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor;
import de.solarisbank.identhub.data.network.interceptor.UserAgentInterceptor;
import de.solarisbank.identhub.domain.session.SessionUrlRepository;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor.DUMMY_BASE_URL;

public final class NetworkModule {
    private static final long TIMEOUT = 60L;

    public DynamicBaseUrlInterceptor provideDynamicBaseUrlInterceptor(SessionUrlRepository sessionUrlRepository) {
        return new DynamicBaseUrlInterceptor(sessionUrlRepository);
    }

    public MoshiConverterFactory provideMoshiConverterFactory() {
        return MoshiConverterFactory.create(
                new Moshi.Builder()
                        .add(Date.class, new Rfc3339DateJsonAdapter())
                        .add(new KotlinJsonAdapterFactory())
                        .build()
        );
    }

    public CallAdapter.Factory provideRxJavaCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    public OkHttpClient provideOkHttpClient(final DynamicBaseUrlInterceptor dynamicBaseUrlInterceptor) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (dynamicBaseUrlInterceptor != null) {
            builder.addInterceptor(dynamicBaseUrlInterceptor);
        }
        return builder
                .addInterceptor(new UserAgentInterceptor())
                .addInterceptor(logging)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public Retrofit provideRetrofit(
            final MoshiConverterFactory moshiConverterFactory,
            final OkHttpClient okHttpClient,
            final CallAdapter.Factory rxJava2CallAdapterFactory,
            final String url
    ) {
        Retrofit.Builder builder = new Retrofit.Builder();
        if (url != null) {
            builder.baseUrl(url);
        } else {
            builder.baseUrl(DUMMY_BASE_URL);
        }
        return builder
                .addConverterFactory(moshiConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(okHttpClient)
                .build();
    }

    public static Retrofit provideSimpleRetrofit(String url) {
        NetworkModule networkModule = new NetworkModule();
        OkHttpClient okHttpClient = networkModule.provideOkHttpClient(null);
        MoshiConverterFactory moshiConverterFactory = networkModule.provideMoshiConverterFactory();
        CallAdapter.Factory callAdapterFactory = networkModule.provideRxJavaCallAdapterFactory();
        return networkModule.provideRetrofit(moshiConverterFactory, okHttpClient, callAdapterFactory, url);
    }
}
