package de.solarisbank.identhub.domain.verification.phone;

import org.jetbrains.annotations.NotNull;

import de.solarisbank.identhub.data.TransactionAuthenticationNumber;
import de.solarisbank.identhub.data.verification.phone.model.VerificationPhoneResponse;
import de.solarisbank.identhub.domain.usecase.SingleUseCase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ConfirmVerificationPhoneUseCase extends SingleUseCase<String, VerificationPhoneResponse> {
    private final VerificationPhoneRepository verificationPhoneRepository;

    public ConfirmVerificationPhoneUseCase(VerificationPhoneRepository verificationPhoneRepository) {
        this.verificationPhoneRepository = verificationPhoneRepository;
    }

    @NotNull
    @Override
    protected Single<VerificationPhoneResponse> invoke(String confirmToken) {
        return verificationPhoneRepository.confirmToken(new TransactionAuthenticationNumber(confirmToken))
                .observeOn(AndroidSchedulers.mainThread());
    }
}
