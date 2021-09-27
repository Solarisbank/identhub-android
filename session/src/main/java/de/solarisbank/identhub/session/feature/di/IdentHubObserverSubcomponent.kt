package de.solarisbank.identhub.session.feature.di

import de.solarisbank.identhub.session.feature.IdentHubSessionObserver

interface IdentHubObserverSubcomponent {

        fun inject(identHubSessionObserver: IdentHubSessionObserver)

        interface Factory {
            fun create(): IdentHubObserverSubcomponent
        }

}
