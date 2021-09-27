package de.solarisbank.identhub.session.data.contract;

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber;
import de.solarisbank.sdk.data.dto.IdentificationDto;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ContractSignRetrofitDataSource implements ContractSignNetworkDataSource {

    private final ContractSignApi contractSignApi;

    public ContractSignRetrofitDataSource(final ContractSignApi contractSignApi) {
        this.contractSignApi = contractSignApi;
    }

    @Override
    public Single<IdentificationDto> postAuthorize(final String identificationId) {
        return contractSignApi.postAuthorize(identificationId)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<IdentificationDto> postConfirm(final String identificationId, final TransactionAuthenticationNumber tan) {
        return contractSignApi.postConfirm(identificationId, tan)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Response<ResponseBody>> fetchDocumentFile(String documentId) {
        return contractSignApi.getDocumentFile(documentId)
                .subscribeOn(Schedulers.io());
    }
}
