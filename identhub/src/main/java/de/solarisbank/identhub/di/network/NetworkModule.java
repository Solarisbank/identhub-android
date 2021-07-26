package de.solarisbank.identhub.di.network;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor;
import de.solarisbank.identhub.data.network.interceptor.UserAgentInterceptor;
import de.solarisbank.identhub.domain.session.SessionUrlRepository;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static de.solarisbank.identhub.data.network.interceptor.DynamicBaseUrlInterceptor.DUMMY_BASE_URL;

public final class NetworkModule {
    private static final long TIMEOUT = 90L;

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

    public static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public CallAdapter.Factory provideRxJavaCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    public UserAgentInterceptor provideUserAgentInterceptor() {
        return new UserAgentInterceptor();
    }

    public OkHttpClient provideOkHttpClient(List<? extends @NotNull Interceptor> interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }

        return builder
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public Retrofit provideRetrofit(
            final MoshiConverterFactory moshiConverterFactory,
            final OkHttpClient okHttpClient,
            final CallAdapter.Factory rxJava2CallAdapterFactory
    ) {
        return new Retrofit.Builder()
                .baseUrl(DUMMY_BASE_URL)
                .addConverterFactory(moshiConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(okHttpClient)
                .build();
    }

}
