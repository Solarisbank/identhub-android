package de.solarisbank.identhub.session.data.verification.phone;

import de.solarisbank.identhub.session.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.session.data.verification.phone.model.VerificationPhoneResponse;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VerificationPhoneApi {
    @POST("mobile_number/authorize")
    Single<VerificationPhoneResponse> postAuthorize();

    @POST("mobile_number/confirm")
    Single<VerificationPhoneResponse> postConfirm(@Body final TransactionAuthenticationNumber tan);
}
