package de.solarisbank.identhub.session.data.contract;

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber;
import de.solarisbank.sdk.data.dto.IdentificationDto;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;

public interface ContractSignNetworkDataSource {
    Single<IdentificationDto> postAuthorize(final String identificationId);

    Single<IdentificationDto> postConfirm(final String identificationId, final TransactionAuthenticationNumber tan);

    Single<Response<ResponseBody>> fetchDocumentFile(final String documentId);
}
