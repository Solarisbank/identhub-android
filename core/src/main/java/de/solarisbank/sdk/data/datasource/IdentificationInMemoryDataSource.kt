package de.solarisbank.sdk.data.datasource

import de.solarisbank.sdk.data.dto.IdentificationDto
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.locks.ReentrantReadWriteLock

class IdentificationInMemoryDataSource : IdentificationLocalDataSource {

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
    private var identificationDto: IdentificationDto? = null
    override suspend fun getIdentification(): IdentificationDto? {
        lock.readLock().lock()
        try {
            return identificationDto
        } finally {
            lock.readLock().unlock()
        }
    }

    override suspend fun storeIdentification(identification: IdentificationDto) {
        lock.writeLock().lock()
        try {
            this.identificationDto = identification
        } finally {
            lock.writeLock().unlock()
        }
    }



    override fun obtainIdentificationDto(): Single<IdentificationDto> {
        lock.readLock().lock()
        try {
            return if (identificationDto != null) {
                Single.just(identificationDto)
            } else {
                Single.error(NullPointerException())
            }
        } finally {
            lock.readLock().unlock()
        }
    }

    override fun deleteIdentification(): Completable {
        return Completable.create {
            lock.writeLock().lock()
            try {
                identificationDto = null
            } finally {
                lock.writeLock().unlock()
            }
        }
    }

    override fun saveIdentification(identificationDto: IdentificationDto): Completable {
        lock.writeLock().lock()
        try {
            this.identificationDto = identificationDto
            return Completable.complete()
        } finally {
            lock.writeLock().unlock()
        }
    }
}