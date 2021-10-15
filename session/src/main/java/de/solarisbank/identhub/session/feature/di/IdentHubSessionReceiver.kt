package de.solarisbank.identhub.session.feature.di

import de.solarisbank.identhub.session.feature.navigation.SessionStepResult

interface IdentHubSessionReceiver {

    fun setSessionResult(sessionStepResult: SessionStepResult)
}