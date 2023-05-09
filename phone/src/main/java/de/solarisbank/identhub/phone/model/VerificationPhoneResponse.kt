package de.solarisbank.identhub.phone.model

data class VerificationPhoneResponse(val id: String, val number: String, val isVerified: Boolean)