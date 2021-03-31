package de.solarisbank.identhub.data.verification.bank.factory;

import de.solarisbank.identhub.data.dao.DocumentDao;
import de.solarisbank.identhub.data.dao.IdentificationDao;
import de.solarisbank.identhub.data.verification.bank.VerificationBankLocalDataSource;
import de.solarisbank.identhub.data.verification.bank.VerificationBankModule;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;

public class VerificationBankLocalDataSourceFactory implements Factory<VerificationBankLocalDataSource> {

    private final VerificationBankModule verificationBankModule;
    private final Provider<IdentificationDao> identificationDaoProvider;
    private final Provider<DocumentDao> documentDaoProvider;

    public VerificationBankLocalDataSourceFactory(
            Provider<DocumentDao> documentDaoProvider,
            Provider<IdentificationDao> identificationDaoProvider,
            VerificationBankModule verificationBankModule) {
        this.documentDaoProvider = documentDaoProvider;
        this.identificationDaoProvider = identificationDaoProvider;
        this.verificationBankModule = verificationBankModule;
    }

    public static VerificationBankLocalDataSourceFactory create(
            Provider<DocumentDao> documentDaoProvider,
            Provider<IdentificationDao> identificationDaoProvider,
            VerificationBankModule verificationBankModule
    ) {
        return new VerificationBankLocalDataSourceFactory(documentDaoProvider, identificationDaoProvider, verificationBankModule);
    }

    @Override
    public VerificationBankLocalDataSource get() {
        return Preconditions.checkNotNull(
                verificationBankModule.provideVerificationBankLocalDataSource(documentDaoProvider.get(), identificationDaoProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
