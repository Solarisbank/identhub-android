package de.solarisbank.sdk.fourthline.domain.kyc.delete

import android.content.Context
import de.solarisbank.sdk.core.di.internal.Factory
import de.solarisbank.sdk.core.di.internal.Provider

class DeleteKycInfoUseCaseFactory private constructor(
    private val applicationContextProvider: Provider<Context>
) : Factory<DeleteKycInfoUseCase>{

    override fun get(): DeleteKycInfoUseCase {
        return DeleteKycInfoUseCase(applicationContextProvider.get())
    }

    companion object {
        @JvmStatic
        fun create(applicationContextProvider: Provider<Context>): DeleteKycInfoUseCaseFactory{
            return DeleteKycInfoUseCaseFactory(applicationContextProvider)
        }
    }
}