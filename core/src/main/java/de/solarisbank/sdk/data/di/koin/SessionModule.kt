package de.solarisbank.sdk.data.di.koin

import android.net.Uri
import de.solarisbank.sdk.data.datasource.SessionUrlLocalDataSource
import de.solarisbank.sdk.data.di.datasource.SessionUrlMemoryDataSource
import de.solarisbank.sdk.data.repository.SessionUrlDataSourceRepository
import de.solarisbank.sdk.data.repository.SessionUrlRepository
import org.koin.core.module.Module
import org.koin.dsl.module
import java.net.URI

internal object SessionModule {
    fun get(sessionUrl: String): Module {
        val apiUrl = buildApiUrl(sessionUrl)
        return module {
            single<SessionUrlLocalDataSource> {
                SessionUrlMemoryDataSource().apply {
                    store(apiUrl)
                }
            }

            single<SessionUrlRepository> {
                SessionUrlDataSourceRepository(get())
            }
        }
    }
}

private fun buildApiUrl(url: String): String {
    val uri = URI.create(url)
    if (uri.authority.contains("-api")) {
        return uri.authority
    } else {
        val domain = uri.authority.replaceFirst(".", "-api.")
        val apiUri = Uri.Builder().authority(domain)
            .scheme(uri.scheme)
            .appendEncodedPath("person_onboarding")
            .appendEncodedPath(uri.path.substring(1))
            .build()

        var apiStringUrl = apiUri.toString()
        if (apiStringUrl.last() != '/') {
            apiStringUrl = "$apiStringUrl/"
        }

        return apiStringUrl
    }
}