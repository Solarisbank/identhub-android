package de.solarisbank.identhub.domain.di.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.identhub.domain.contract.FetchPdfUseCase
import de.solarisbank.identhub.file.FileController
import de.solarisbank.sdk.feature.di.internal.Factory
import de.solarisbank.sdk.feature.di.internal.Preconditions
import de.solarisbank.sdk.feature.di.internal.Provider

class FetchPdfUseCaseFactory(
    private val contractSignRepositoryProvider: Provider<ContractSignRepository>,
    private val fileControllerProvider: Provider<FileController>
) : Factory<FetchPdfUseCase> {
    override fun get(): FetchPdfUseCase {
        return Preconditions.checkNotNull(
            FetchPdfUseCase(contractSignRepositoryProvider.get(), fileControllerProvider.get()),
            "Cannot return null from provider method"
        )
    }

    companion object {
        @JvmStatic
        fun create(
            contractSignRepositoryProvider: Provider<ContractSignRepository>,
            fileControllerProvider: Provider<FileController>
        ): FetchPdfUseCaseFactory {
            return FetchPdfUseCaseFactory(contractSignRepositoryProvider, fileControllerProvider)
        }
    }
}