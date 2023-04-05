package de.solarisbank.identhub.mockro.shared

import de.solarisbank.sdk.data.dto.DocumentDto
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.entity.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val baseFakeIdent = IdentificationDto(
    id = "someFakeIdentId",
    reference = "someFakeReference",
    url = null,
    status = "pending",
    method = "fourthline_signing",
    documents = null
)

object MockroIdentification {
    private var current = baseFakeIdent
    private val scope = CoroutineScope(Dispatchers.IO)

    fun get() = current

    fun change(change: IdentificationChange): IdentificationDto {
        when(change) {
            is IdentificationChange.GoToFourthline ->
                current = current.copy(
                    status = "identification_data_required",
                    nextStep = PersonaConfig.current.fourthlineStep
                )
            is IdentificationChange.GoToQes ->
                if (PersonaConfig.current.qesStep == null) {
                    current = change(IdentificationChange.Success)
                } else {
                    current = current.copy(
                        status = "authorization_required",
                        nextStep = PersonaConfig.current.qesStep,
                        documents = listOf(
                            DocumentDto(
                                id = "documentId",
                                name = "TestDocument.pdf",
                                contentType = "file/pdf",
                                documentType = "",
                                size = 10000,
                                isCustomerAccessible = true
                            )
                        )
                    )
                }
            is IdentificationChange.Confirmed ->
                current = current.copy(
                    status = Status.CONFIRMED.label,
                    nextStep = null
                )
            is IdentificationChange.Success ->
                current = current.copy(
                    status = Status.SUCCESSFUL.label,
                    nextStep = null
                )
            is IdentificationChange.Custom ->
                current = change.modify.invoke(current)
        }
        return current
    }

    fun changeWithDelay(change: IdentificationChange, delayMillis: Long = 500L) {
        scope.launch {
            delay(delayMillis)
            launch(Dispatchers.Main) {
                change(change)
            }
        }
    }
}

sealed class IdentificationChange {
    class Custom(val modify: IdentificationDto.() -> IdentificationDto): IdentificationChange()
    object GoToFourthline: IdentificationChange()
    object GoToQes: IdentificationChange()
    object Confirmed: IdentificationChange()
    object Success: IdentificationChange()
}