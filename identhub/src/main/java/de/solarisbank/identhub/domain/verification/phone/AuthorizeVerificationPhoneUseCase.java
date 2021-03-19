package de.solarisbank.identhub.domain.verification.phone;

import org.jetbrains.annotations.NotNull;

import de.solarisbank.identhub.data.verification.phone.model.VerificationPhoneResponse;
import de.solarisbank.identhub.domain.usecase.SingleUseCase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AuthorizeVerificationPhoneUseCase extends SingleUseCase<Void, VerificationPhoneResponse> {
    private final VerificationPhoneRepository verificationPhoneRepository;

    public AuthorizeVerificationPhoneUseCase(VerificationPhoneRepository verificationPhoneRepository) {
        this.verificationPhoneRepository = verificationPhoneRepository;
    }

    @NotNull
    @Override
    protected Single<VerificationPhoneResponse> invoke(Void param) {
        return verificationPhoneRepository.authorize()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
