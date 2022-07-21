package de.solarisbank.identhub.session.data.datasource

import de.solarisbank.sdk.data.dto.InitializationDto
import timber.log.Timber
import java.util.concurrent.locks.ReentrantReadWriteLock

class IdentityInitializationInMemoryDataSource : IdentityInitializationDataSource {

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
    private var initializationDto: InitializationDto? = null

    override fun saveInitializationDto(initializationDto: InitializationDto) {
        Timber.d("saveInitializationDto() data: $initializationDto")
        lock.writeLock().lock()
        try {
            this.initializationDto = initializationDto
        } finally {
            lock.writeLock().unlock()
        }
    }

    override fun getInitializationDto(): InitializationDto? {
        lock.readLock().lock()
        try {
            return initializationDto
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun deleteInitializationDto() {
        Timber.d("deleteInitializationDto()")
        lock.writeLock().lock()
        try {
            this.initializationDto = null
        } finally {
            lock.writeLock().unlock()
        }
    }
}