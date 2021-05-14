package de.solarisbank.identhub.session

interface IdentHubObserverSubcomponent {

        fun inject(identHubSessionObserver: IdentHubSessionObserver)

        interface Factory {
            fun create(): IdentHubObserverSubcomponent
        }

}
