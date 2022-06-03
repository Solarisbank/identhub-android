package de.solarisbank.identhub.session.data.repository

import de.solarisbank.identhub.session.data.datasource.SessionStateSavedStateHandleDataSource
import de.solarisbank.identhub.session.data.dto.SessionStateDto
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

//todo add interface abstraction
class SessionStateRepository internal constructor(
    private val sessionStateSavedStateHandleDataSource: SessionStateSavedStateHandleDataSource
    ) {

    fun getSessionStateDto(): Single<SessionStateDto> {
        return sessionStateSavedStateHandleDataSource.getSessionStateDto()
    }

    fun deleteSessionStateDto():Completable {
        return sessionStateSavedStateHandleDataSource.deleteSessionStateDto()
    }

    fun saveSessionStateDto(
        started: Boolean? = null,
        resumed: Boolean? = null,
        identificationId: String? = null,
        lastNestStep: String? = null
    ): Completable {
        Timber.d("saveSessionStateDto")
        return sessionStateSavedStateHandleDataSource.saveSessionStateDto(
            sessionStateSavedStateHandleDataSource.getSessionStateDto().map { oldSessionStateDto ->
                oldSessionStateDto.copy(
                    started = started ?: oldSessionStateDto.started,
                    resumed = resumed ?: oldSessionStateDto.resumed
                )
            }.blockingGet()
        )
    }

    fun saveSessionStateDto(sessionStateDto: SessionStateDto): Completable {
        return sessionStateSavedStateHandleDataSource.saveSessionStateDto(sessionStateDto)
    }

}