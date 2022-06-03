package de.solarisbank.identhub.session

import de.solarisbank.identhub.session.feature.IdentHubSession

interface SessionCooperator {

    fun sessionWithUrl(url: String): IdentHubSession

    fun initMainProcess() {

    }
}