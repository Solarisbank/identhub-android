package de.solarisbank.identhub.domain.verification.bank;

import de.solarisbank.identhub.session.data.Mapper;
import de.solarisbank.sdk.data.dto.IdentificationDto;
import de.solarisbank.sdk.data.entity.IdentificationWithDocument;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

public class FetchingAuthorizedIBanStatusUseCaseFactory implements Factory<FetchingAuthorizedIBanStatusUseCase> {

    private final Provider<VerificationBankRepository> verificationBankRepositoryProvider;
    private final Provider<Mapper<IdentificationDto, IdentificationWithDocument>> identificationEntityMapper;

    public FetchingAuthorizedIBanStatusUseCaseFactory(Provider<Mapper<IdentificationDto, IdentificationWithDocument>> identificationEntityMapper,
                                                      Provider<VerificationBankRepository> verificationBankRepositoryProvider) {
        this.identificationEntityMapper = identificationEntityMapper;
        this.verificationBankRepositoryProvider = verificationBankRepositoryProvider;
    }

    public static FetchingAuthorizedIBanStatusUseCaseFactory create(Provider<Mapper<IdentificationDto, IdentificationWithDocument>> identificationEntityMapper,
                                                                    Provider<VerificationBankRepository> verificationBankRepositoryProvider
    ) {
        return new FetchingAuthorizedIBanStatusUseCaseFactory(identificationEntityMapper, verificationBankRepositoryProvider);
    }

    @Override
    public FetchingAuthorizedIBanStatusUseCase get() {
        return Preconditions.checkNotNull(
                new FetchingAuthorizedIBanStatusUseCase(identificationEntityMapper.get(), verificationBankRepositoryProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
