package de.solarisbank.identhub.session.feature.di

import de.solarisbank.identhub.session.IdentHub
import de.solarisbank.sdk.feature.di.internal.DoubleCheck
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Provider

class SessionNavigationSubComponentImpl {

    private var identHubSessionReceiverProvider: Provider<IdentHubSessionReceiver> = DoubleCheck.provider(
        object : Factory<IdentHubSessionReceiver> {
            override fun get(): IdentHubSessionReceiver {
                return IdentHub
            }
        })
}