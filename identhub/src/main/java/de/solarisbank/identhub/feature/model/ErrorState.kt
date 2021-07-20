package de.solarisbank.identhub.feature.model

/**
 * Indicates, that AlertDialog should be shown
 */
interface ErrorState {
    val dialogTitle: String
    val dialogMessage: String
    val dialogPositiveLabel: String
    val dialogNegativeLabel: String?
}