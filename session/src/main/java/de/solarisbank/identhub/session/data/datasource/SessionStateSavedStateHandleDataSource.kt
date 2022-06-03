package de.solarisbank.identhub.session.data.datasource

import androidx.lifecycle.SavedStateHandle
import de.solarisbank.identhub.session.data.dto.SessionStateDto
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.locks.ReentrantReadWriteLock

interface SessionStateSavedDataSource {

    fun getSessionStateDto(): Single<SessionStateDto>

    fun saveSessionStateDto(sessionStateDto: SessionStateDto): Completable

    fun deleteSessionStateDto(): Completable
}

class SessionStateSavedStateHandleDataSource(
    private val savedStateHandle: SavedStateHandle
    ) : SessionStateSavedDataSource {

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    override fun getSessionStateDto(): Single<SessionStateDto> {
        Timber.d("getSessionStateDto()0")
        lock.readLock().lock()
        try {
            val result = Single.just(
                SessionStateDto(
                    started = if (savedStateHandle.contains(STARTED)) {
                        savedStateHandle.get<Boolean>(STARTED)!!
                    } else {
                        false
                    },
                    resumed = if (savedStateHandle.contains(RESUMED)) {
                        savedStateHandle.get<Boolean>(RESUMED)!!
                    } else {
                        false
                    }
                )
            )
                .doOnSuccess { Timber.d("getSessionStateDto()1, result : $it") }
                .doOnError { Timber.d("getSessionStateDto()2, result : $it") }
            return result
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun saveSessionStateDto(sessionStateDto: SessionStateDto): Completable {
        return Completable.fromAction {
            Timber.d("updateNextStep, sessionStateDto : $sessionStateDto")
            lock.writeLock().lock()
            try {
                savedStateHandle.set(STARTED, sessionStateDto.started)
                savedStateHandle.set(RESUMED, sessionStateDto.resumed)
            } finally {
                lock.writeLock().unlock()
            }
        }
    }

    override fun deleteSessionStateDto(): Completable {
        Timber.d("deleteSessionStateDto")
        return Completable.fromAction {
            lock.writeLock().lock()
            try {
                savedStateHandle.remove<Boolean>(STARTED)
                savedStateHandle.remove<Boolean>(RESUMED)
            } finally {
                lock.writeLock().unlock()
            }
        }
    }

    companion object {
        private const val STARTED = "STARTED"
        private const val RESUMED = "RESUMED"
    }
}