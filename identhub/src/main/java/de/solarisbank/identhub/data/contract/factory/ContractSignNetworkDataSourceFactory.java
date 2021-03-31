package de.solarisbank.identhub.data.contract.factory;

import de.solarisbank.identhub.data.contract.ContractSignApi;
import de.solarisbank.identhub.data.contract.ContractSignModule;
import de.solarisbank.identhub.data.contract.ContractSignNetworkDataSource;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;

public class ContractSignNetworkDataSourceFactory implements Factory<ContractSignNetworkDataSource> {

    private final ContractSignModule contractSignModule;
    private final Provider<ContractSignApi> contractSignApiProvider;

    public ContractSignNetworkDataSourceFactory(
            Provider<ContractSignApi> contractSignApiProvider,
            ContractSignModule contractSignModule) {
        this.contractSignModule = contractSignModule;
        this.contractSignApiProvider = contractSignApiProvider;
    }

    public static ContractSignNetworkDataSourceFactory create(ContractSignModule contractSignModule, Provider<ContractSignApi> contractSignApiProvider) {
        return new ContractSignNetworkDataSourceFactory(contractSignApiProvider, contractSignModule);
    }

    @Override
    public ContractSignNetworkDataSource get() {
        return Preconditions.checkNotNull(
                contractSignModule.provideContractSignNetworkDataSource(contractSignApiProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
