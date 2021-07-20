package de.solarisbank.identhub.data.contract;

import de.solarisbank.identhub.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.domain.data.dto.IdentificationDto;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface ContractSignApi {
    @PATCH("sign_documents/{identification_uid}/authorize")
    Single<IdentificationDto> postAuthorize(@Path("identification_uid") final String identificationId);

    @PATCH("sign_documents/{identification_uid}/confirm")
    Single<IdentificationDto> postConfirm(@Path("identification_uid") final String identificationId, @Body final TransactionAuthenticationNumber tan);

    @GET("sign_documents/{document_uid}/download")
    Single<Response<ResponseBody>> getDocumentFile(@Path("document_uid") final String documentId);
}
