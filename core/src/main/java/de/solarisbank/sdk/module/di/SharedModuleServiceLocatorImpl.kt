package de.solarisbank.sdk.module.di

import de.solarisbank.sdk.core_ui.data.dto.Customization
import okhttp3.OkHttpClient

class SharedModuleServiceLocatorImpl(
        override var okhttpClient: OkHttpClient,
        override var customization: Customization)
    : SharedModuleServiceLocator