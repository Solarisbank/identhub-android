package de.solarisbank.identhub.bank.domain.api;

import de.solarisbank.identhub.bank.data.Iban;
import de.solarisbank.sdk.data.dto.IdentificationDto;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VerificationBankApi {
    @POST("iban/verify")
    Single<IdentificationDto> postVerify(@Body final Iban iBan);

    @POST("/bank_id_identification")
    Single<IdentificationDto> postBankIdIdentification(@Body final Iban iBan);

    @GET("identifications/{identification_uid}")
    Single<IdentificationDto> getVerification(@Path("identification_uid") final String identificationId);
}
