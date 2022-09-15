package de.solarisbank.identhub.phone

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import io.kotest.core.config.AbstractProjectConfig
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

object ProjectConfig : AbstractProjectConfig() {
    override fun beforeAll() {
        super.beforeAll()
        setUpLiveData()
        setUpRxJava()
    }

    override fun afterAll() {
        super.afterAll()
        resetLiveData()
        resetRxJava()
    }

    private fun setUpLiveData() {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    private fun setUpRxJava() {
        RxAndroidPlugins.reset()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
    }

    private fun resetLiveData() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    private fun resetRxJava() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }
}