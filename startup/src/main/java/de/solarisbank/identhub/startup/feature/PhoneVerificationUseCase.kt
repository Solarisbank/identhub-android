package de.solarisbank.identhub.startup.feature

import io.reactivex.Completable

interface PhoneVerificationUseCase {
    fun verifyToken(token: String): Completable
    fun authorize(): Completable
}