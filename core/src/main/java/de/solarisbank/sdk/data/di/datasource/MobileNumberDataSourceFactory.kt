package de.solarisbank.sdk.data.di.datasource

import de.solarisbank.sdk.data.api.MobileNumberApi
import de.solarisbank.sdk.data.datasource.MobileNumberDataSource
import de.solarisbank.sdk.feature.di.internal.Factory

class MobileNumberDataSourceFactory private constructor(private val mobileNumberApi: MobileNumberApi) :
    Factory<MobileNumberDataSource> {

    override fun get(): MobileNumberDataSource {
        return MobileNumberDataSource(mobileNumberApi)
    }

    companion object {
        @JvmStatic
        fun create(mobileNumberApi: MobileNumberApi): MobileNumberDataSourceFactory {
            return MobileNumberDataSourceFactory(mobileNumberApi)
        }
    }
}