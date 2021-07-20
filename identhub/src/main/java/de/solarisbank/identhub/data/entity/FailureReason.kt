package de.solarisbank.identhub.data.entity

import timber.log.Timber

enum class FailureReason(val label: String) {
    ACCESS_BY_AUTHORIZED_HOLDER("access_by_authorized_holder"),
    ACCOUNT_SNAPSHOT_FAILED("account_snapshot_failed"),
    EXPIRED("expired"),
    JOINT_ACCOUNT("joint_account");

    companion object {
        fun getEnum(label: String?): FailureReason? {
            for (reason in values()) {
                if (reason.label.equals(label, ignoreCase = true)) {
                    return reason
                }
            }
            Timber.e("FailureReason label: $label")
            return null
        }
    }
}