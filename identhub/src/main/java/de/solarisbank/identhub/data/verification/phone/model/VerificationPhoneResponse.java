package de.solarisbank.identhub.data.verification.phone.model;

import java.util.Objects;

public class VerificationPhoneResponse {
    private final String id;
    private final String number;
    private final boolean verified;

    public VerificationPhoneResponse(String id, String number, boolean verified) {
        this.id = id;
        this.number = number;
        this.verified = verified;
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public boolean isVerified() {
        return verified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationPhoneResponse that = (VerificationPhoneResponse) o;
        return verified == that.verified &&
                id.equals(that.id) &&
                number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, verified);
    }
}
