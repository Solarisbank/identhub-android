package de.solarisbank.identhub.di.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.solarisbank.sdk.data.di.network.NetworkModule
import de.solarisbank.sdk.data.network.interceptor.DynamicBaseUrlInterceptor
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class NetworkModuleTestFactory(val mockWebServer: MockWebServer) {
    private val TIMEOUT = 90L

    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(Date::class.java, Rfc3339DateJsonAdapter())
                    .add(KotlinJsonAdapterFactory())
                    .build()
            )
        )
        .build()

    fun provideNetworkModule(): NetworkModule {
        return mockk<NetworkModule> {
            every { provideDynamicBaseUrlInterceptor(any()) } returns mockk<DynamicBaseUrlInterceptor>()
            every { provideRetrofit(any(), any(), any()) } returns provideMockedRetrofit()
            every { provideMoshiConverterFactory() } returns MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(Date::class.java, Rfc3339DateJsonAdapter())
                    .add(KotlinJsonAdapterFactory())
                    .build()
            )
            every { provideRxJavaCallAdapterFactory() } returns RxJava2CallAdapterFactory.create()
            every { provideOkHttpClient(any()) } returns okHttpClient
        }
    }

    private fun provideMockedRetrofit(): Retrofit {
        println("NetworkModuleTestFactory provideMockedRetrofit()")
        return retrofit
    }


}