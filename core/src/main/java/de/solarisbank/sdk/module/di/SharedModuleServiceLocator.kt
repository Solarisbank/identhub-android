package de.solarisbank.sdk.module.di

import de.solarisbank.sdk.core_ui.data.dto.Customization
import okhttp3.OkHttpClient

interface SharedModuleServiceLocator {
    var okhttpClient: OkHttpClient
    var customization: Customization
}