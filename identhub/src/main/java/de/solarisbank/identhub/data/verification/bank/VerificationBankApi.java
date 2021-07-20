package de.solarisbank.identhub.data.verification.bank;

import de.solarisbank.identhub.data.verification.bank.model.IBan;
import de.solarisbank.identhub.domain.data.dto.IdentificationDto;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VerificationBankApi {
    @POST("iban/verify")
    Single<IdentificationDto> postVerify(@Body final IBan iBan);

    @POST("/bank_id_identification")
    Single<IdentificationDto> postBankIdIdentification(@Body final IBan iBan);

    @GET("identifications/{identification_uid}")
    Single<IdentificationDto> getVerification(@Path("identification_uid") final String identificationId);
}
