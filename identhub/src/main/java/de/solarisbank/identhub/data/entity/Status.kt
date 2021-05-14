package de.solarisbank.identhub.data.entity

enum class Status(val label: String) {
    CREATED("created"),
    PENDING("pending"),
    AUTHORIZATION_REQUIRED("authorization_required"),
    CONFIRMATION_REQUIRED("confirmation_required"),
    PROCESSED("processed"),
    CONFIRMED("confirmed"),
    SUCCESSFUL("successful"),
    FAILED("failed");

    companion object {
        fun getEnum(label: String?): Status? {
            for (status in values()) {
                if (status.label.equals(label, ignoreCase = true)) {
                    return status
                }
            }
            return null
        }
    }
}