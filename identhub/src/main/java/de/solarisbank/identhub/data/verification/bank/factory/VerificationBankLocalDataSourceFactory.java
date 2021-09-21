package de.solarisbank.identhub.data.verification.bank.factory;

import de.solarisbank.identhub.data.verification.bank.VerificationBankDataModule;
import de.solarisbank.identhub.data.verification.bank.VerificationBankLocalDataSource;
import de.solarisbank.sdk.core.di.internal.Factory;
import de.solarisbank.sdk.core.di.internal.Preconditions;
import de.solarisbank.sdk.core.di.internal.Provider;
import de.solarisbank.sdk.data.dao.DocumentDao;
import de.solarisbank.sdk.data.dao.IdentificationDao;

public class VerificationBankLocalDataSourceFactory implements Factory<VerificationBankLocalDataSource> {

    private final VerificationBankDataModule verificationBankDataModule;
    private final Provider<IdentificationDao> identificationDaoProvider;
    private final Provider<DocumentDao> documentDaoProvider;

    public VerificationBankLocalDataSourceFactory(
            Provider<DocumentDao> documentDaoProvider,
            Provider<IdentificationDao> identificationDaoProvider,
            VerificationBankDataModule verificationBankDataModule) {
        this.documentDaoProvider = documentDaoProvider;
        this.identificationDaoProvider = identificationDaoProvider;
        this.verificationBankDataModule = verificationBankDataModule;
    }

    public static VerificationBankLocalDataSourceFactory create(
            Provider<DocumentDao> documentDaoProvider,
            Provider<IdentificationDao> identificationDaoProvider,
            VerificationBankDataModule verificationBankDataModule
    ) {
        return new VerificationBankLocalDataSourceFactory(documentDaoProvider, identificationDaoProvider, verificationBankDataModule);
    }

    @Override
    public VerificationBankLocalDataSource get() {
        return Preconditions.checkNotNull(
                verificationBankDataModule.provideVerificationBankLocalDataSource(documentDaoProvider.get(), identificationDaoProvider.get()),
                "Cannot return null from provider method"
        );
    }
}
