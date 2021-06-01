package de.solarisbank.identhub.data.entity

import timber.log.Timber

enum class Status(val label: String) {
    PENDING("pending"),
    PROCESSED("processed"),
    SUCCESSFUL("successful"),
    FAILED("failed"),
    CREATED("created"),
    PENDING_SUCCESSFUL("pending_successful"),
    ABORTED("aborted"),
    CANCELED("canceled"),
    EXPIRED("expired"),
    AUTHORIZATION_REQUIRED("authorization_required"),
    CONFIRMATION_REQUIRED("confirmation_required"),
    CONFIRMED("confirmed"),
    SIGNED("signed"),
    IDENTIFICATION_DATA_REQUIRED("identification_data_required"),

    UPLOAD("upload"),
    INTERNAL_ERROR("internal_error"); //internal usage for network connection and server errors

    companion object {
        fun getEnum(label: String?): Status? {
            for (status in values()) {
                if (status.label.equals(label, ignoreCase = true)) {
                    return status
                }
            }
            Timber.e("Unsupported status label: $label")
            return null
        }
    }
}