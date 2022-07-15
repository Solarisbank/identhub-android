package de.solarisbank.sdk.logger

import de.solarisbank.sdk.logger.domain.model.LogJson
import java.util.concurrent.locks.ReentrantReadWriteLock

open class RWLockList(var data: ArrayList<LogJson>) {
    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()


    fun add(entry: LogJson) {
        lock.writeLock().lock()
        data.add(entry)
        lock.writeLock().unlock()
    }


    fun getAndClear(): List<LogJson> {
        lock.writeLock().lock()
        val tempData = data
        data = ArrayList()
        lock.writeLock().unlock()
        return tempData


    }

}