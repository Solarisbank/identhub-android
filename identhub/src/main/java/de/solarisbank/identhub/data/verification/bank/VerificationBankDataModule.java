package de.solarisbank.identhub.data.verification.bank;

import de.solarisbank.identhub.data.Mapper;
import de.solarisbank.identhub.domain.data.dto.IdentificationDto;
import de.solarisbank.identhub.domain.verification.bank.VerificationBankRepository;
import de.solarisbank.sdk.data.dao.DocumentDao;
import de.solarisbank.sdk.data.dao.IdentificationDao;
import de.solarisbank.sdk.data.entity.IdentificationWithDocument;
import retrofit2.Retrofit;

public final class VerificationBankDataModule {
    public VerificationBankApi provideVerificationBankApi(final Retrofit retrofit) {
        return retrofit.create(VerificationBankApi.class);
    }

    public VerificationBankNetworkDataSource provideVerificationBankNetworkDataSource(VerificationBankApi verificationBankApi) {
        return new VerificationBankRetrofitDataSource(verificationBankApi);
    }

    public VerificationBankLocalDataSource provideVerificationBankLocalDataSource(DocumentDao documentDao, IdentificationDao identificationDao) {
        return new VerificationBankRoomDataSource(documentDao, identificationDao);
    }

    public VerificationBankRepository provideVerificationBankRepository(
            Mapper<IdentificationDto, IdentificationWithDocument> identificationWithDocumentMapper,
            VerificationBankNetworkDataSource verificationBankNetworkDataSource,
            VerificationBankLocalDataSource verificationBankLocalDataSource) {
        return new VerificationBankDataSourceRepository(identificationWithDocumentMapper, verificationBankNetworkDataSource, verificationBankLocalDataSource);
    }
}
