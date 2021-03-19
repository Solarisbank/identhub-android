package de.solarisbank.identhub.data.contract.factory;

import de.solarisbank.identhub.data.contract.ContractSignApi;
import de.solarisbank.identhub.data.contract.ContractSignModule;
import de.solarisbank.identhub.di.internal.Factory;
import de.solarisbank.identhub.di.internal.Preconditions;
import de.solarisbank.identhub.di.internal.Provider;
import retrofit2.Retrofit;

public class ProvideContractSignApiFactory implements Factory<ContractSignApi> {

    private final ContractSignModule contractSignModule;
    private final Provider<Retrofit> retrofitProvider;

    public ProvideContractSignApiFactory(
            Provider<Retrofit> retrofitProvider,
            ContractSignModule contractSignModule) {
        this.contractSignModule = contractSignModule;
        this.retrofitProvider = retrofitProvider;
    }

    public static ProvideContractSignApiFactory create(ContractSignModule contractSignModule, Provider<Retrofit> retrofitProvider) {
        return new ProvideContractSignApiFactory(retrofitProvider, contractSignModule);
    }

    @Override
    public ContractSignApi get() {
        return Preconditions.checkNotNull(
                contractSignModule.provideContractSignApi(retrofitProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
