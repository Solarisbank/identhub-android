package de.solarisbank.sdk.data.di.koin

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.solarisbank.sdk.core.BuildConfig
import de.solarisbank.sdk.data.network.interceptor.DynamicBaseUrlInterceptor
import de.solarisbank.sdk.logger.LoggerHttpInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

private const val NETWORK_TIMEOUT = 90L
private const val DYNAMIC_URL_INTERCEPTOR = "DynamicUrlInterceptor"
private const val LOGGING_INTERCEPTOR = "LoggingInterceptor"
private const val USER_AGENT_INTERCEPTOR = "UserAgentInterceptor"

internal val networkModule = module {
    single {
        MoshiConverterFactory.create(
            Moshi.Builder()
                .add(Date::class.java, Rfc3339DateJsonAdapter())
                .add(KotlinJsonAdapterFactory())
                .build())
    }

    single<Interceptor>(named(DYNAMIC_URL_INTERCEPTOR)) {
        DynamicBaseUrlInterceptor(get())
    }

    single<Interceptor>(named(LOGGING_INTERCEPTOR)) {
        LoggerHttpInterceptor()
    }

    single<Interceptor>(named(USER_AGENT_INTERCEPTOR)) {
        UserAgentInterceptor(get())
    }

    single {
        RxJava2CallAdapterFactory.create()
    }

    single {
        val interceptors = listOf<Interceptor>(
            get(named(DYNAMIC_URL_INTERCEPTOR)),
            get(named(LOGGING_INTERCEPTOR)),
            get(named(USER_AGENT_INTERCEPTOR))
        )
        OkHttpClient.Builder().apply {
            interceptors.forEach {
                addInterceptor(it)
            }
            writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
        }.build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(DynamicBaseUrlInterceptor.DUMMY_BASE_URL)
            .addConverterFactory(get<MoshiConverterFactory>())
            .addCallAdapterFactory(get<RxJava2CallAdapterFactory>())
            .client(get())
            .build()
    }
}

internal class UserAgentInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sdkVersion = BuildConfig.SDK_VERSION
        val appName = loadAppName(context)
        val requestWithUserAgent = chain.request()
            .newBuilder()
            .addHeader("User-Agent", "IdentHub Android ($sdkVersion) - $appName")
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}

internal fun loadAppName(context: Context): String {
    val packageManager = context.packageManager
    var appInfo: ApplicationInfo? = null

    try {
        appInfo = packageManager.getApplicationInfo(context.applicationInfo.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {

    }

    if (appInfo != null) {
        return packageManager.getApplicationLabel(appInfo).toString()
    }
    return ""
}