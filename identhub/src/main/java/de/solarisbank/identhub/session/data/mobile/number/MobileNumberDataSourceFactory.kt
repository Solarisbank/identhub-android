package de.solarisbank.identhub.session.data.mobile.number

import de.solarisbank.sdk.core.di.internal.Factory

class MobileNumberDataSourceFactory private constructor(private val mobileNumberApi: MobileNumberApi) : Factory< MobileNumberDataSource> {

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