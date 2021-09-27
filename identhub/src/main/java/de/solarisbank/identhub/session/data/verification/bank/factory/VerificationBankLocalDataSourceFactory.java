package de.solarisbank.identhub.session.data.verification.bank.factory;

import de.solarisbank.identhub.session.data.verification.bank.VerificationBankDataModule;
import de.solarisbank.identhub.session.data.verification.bank.VerificationBankLocalDataSource;
import de.solarisbank.sdk.data.dao.DocumentDao;
import de.solarisbank.sdk.data.dao.IdentificationDao;
import de.solarisbank.sdk.feature.di.internal.Factory;
import de.solarisbank.sdk.feature.di.internal.Preconditions;
import de.solarisbank.sdk.feature.di.internal.Provider;

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
