package de.solarisbank.identhub.feature.model

/**
 * Indicates, that AlertDialog should be shown
 */
interface ErrorState {
    val dialogTitleId: Int
    val dialogMessageId: Int
    val dialogPositiveLabelId: Int
    val dialogNegativeLabelId: Int?
}