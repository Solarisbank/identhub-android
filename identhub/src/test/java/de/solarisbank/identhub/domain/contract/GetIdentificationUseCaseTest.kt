package de.solarisbank.identhub.domain.contract

import de.solarisbank.identhub.data.contract.ContractSignRepository
import de.solarisbank.sdk.data.dto.IdentificationDto
import de.solarisbank.sdk.data.dto.InitializationDto
import de.solarisbank.sdk.data.dto.PartnerSettingsDto
import de.solarisbank.sdk.data.repository.IdentityInitializationRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single

class GetIdentificationUseCaseTest : StringSpec({

    "defaultToFallbackStep is true, fallback is available"{

        val expectedNextStep = "expectedNextStep"

        val expectedFallbackStep = "expectedFallbackStep"

        val identificationDtoMockk = mockk<IdentificationDto>() {
            every { nextStep } returns expectedNextStep
            every { fallbackStep } returns expectedFallbackStep
        }
        val contractSignRepositoryMockk = mockk<ContractSignRepository>() {
            every { getIdentification() } returns Single.just(identificationDtoMockk)
        }

        val partnerSettingsDtoMockk = mockk<PartnerSettingsDto>() {
            every { defaultToFallbackStep } returns true
        }

        val initializationDtoMockk = mockk<InitializationDto>() {
            every { partnerSettings } returns partnerSettingsDtoMockk
        }

        val identityInitializationRepositoryMockk =
            mockk<IdentityInitializationRepository>() {
                every { getInitializationDto() } returns initializationDtoMockk
            }

        val resultNextStep: String? = GetIdentificationUseCase(
            contractSignRepositoryMockk, identityInitializationRepositoryMockk
        ).execute(Unit).blockingGet().nextStep

        expectedFallbackStep shouldBe resultNextStep
    }

    "defaultToFallbackStep is true, fallback is null"{

        val expectedNextStep = "expectedNextStep"

        val expectedFallbackStep = null

        val identificationDtoMockk = mockk<IdentificationDto>() {
            every { nextStep } returns expectedNextStep
            every { fallbackStep } returns expectedFallbackStep
        }
        val contractSignRepositoryMockk = mockk<ContractSignRepository>() {
            every { getIdentification() } returns Single.just(identificationDtoMockk)
        }

        val partnerSettingsDtoMockk = mockk<PartnerSettingsDto>() {
            every { defaultToFallbackStep } returns true
        }

        val initializationDtoMockk = mockk<InitializationDto>() {
            every { partnerSettings } returns partnerSettingsDtoMockk
        }

        val identityInitializationRepositoryMockk =
            mockk<IdentityInitializationRepository>() {
                every { getInitializationDto() } returns initializationDtoMockk
            }

        val resultNextStep: String? = GetIdentificationUseCase(
            contractSignRepositoryMockk, identityInitializationRepositoryMockk
        ).execute(Unit).blockingGet().nextStep

        expectedNextStep shouldBe resultNextStep
    }

    "defaultToFallbackStep is false, fallback is available"{

        val expectedNextStep = "expectedNextStep"

        val expectedFallbackStep = "expectedFallbackStep"

        val identificationDtoMockk = mockk<IdentificationDto>() {
            every { nextStep } returns expectedNextStep
            every { fallbackStep } returns expectedFallbackStep
        }
        val contractSignRepositoryMockk = mockk<ContractSignRepository>() {
            every { getIdentification() } returns Single.just(identificationDtoMockk)
        }

        val partnerSettingsDtoMockk = mockk<PartnerSettingsDto>() {
            every { defaultToFallbackStep } returns false
        }

        val initializationDtoMockk = mockk<InitializationDto>() {
            every { partnerSettings } returns partnerSettingsDtoMockk
        }

        val identityInitializationRepositoryMockk =
            mockk<IdentityInitializationRepository>() {
                every { getInitializationDto() } returns initializationDtoMockk
            }

        val resultNextStep: String? = GetIdentificationUseCase(
            contractSignRepositoryMockk, identityInitializationRepositoryMockk
        ).execute(Unit).blockingGet().nextStep

        expectedNextStep shouldBe resultNextStep
    }

    "defaultToFallbackStep is false, fallback is null"{

        val expectedNextStep = "expectedNextStep"

        val expectedFallbackStep = null

        val identificationDtoMockk = mockk<IdentificationDto>() {
            every { nextStep } returns expectedNextStep
            every { fallbackStep } returns expectedFallbackStep
        }
        val contractSignRepositoryMockk = mockk<ContractSignRepository>() {
            every { getIdentification() } returns Single.just(identificationDtoMockk)
        }

        val partnerSettingsDtoMockk = mockk<PartnerSettingsDto>() {
            every { defaultToFallbackStep } returns false
        }

        val initializationDtoMockk = mockk<InitializationDto>() {
            every { partnerSettings } returns partnerSettingsDtoMockk
        }

        val identityInitializationRepositoryMockk =
            mockk<IdentityInitializationRepository>() {
                every { getInitializationDto() } returns initializationDtoMockk
            }

        val resultNextStep: String? = GetIdentificationUseCase(
            contractSignRepositoryMockk, identityInitializationRepositoryMockk
        ).execute(Unit).blockingGet().nextStep

        expectedNextStep shouldBe resultNextStep
    }

    "defaultToFallbackStep is null, fallback is available"{

        val expectedNextStep = "expectedNextStep"

        val expectedFallbackStep = null

        val identificationDtoMockk = mockk<IdentificationDto>() {
            every { nextStep } returns expectedNextStep
            every { fallbackStep } returns expectedFallbackStep
        }
        val contractSignRepositoryMockk = mockk<ContractSignRepository>() {
            every { getIdentification() } returns Single.just(identificationDtoMockk)
        }

        val partnerSettingsDtoMockk = mockk<PartnerSettingsDto>() {
            every { defaultToFallbackStep } returns true
        }

        val initializationDtoMockk = mockk<InitializationDto>() {
            every { partnerSettings } returns partnerSettingsDtoMockk
        }

        val identityInitializationRepositoryMockk =
            mockk<IdentityInitializationRepository>() {
                every { getInitializationDto() } returns initializationDtoMockk
            }

        val resultNextStep: String? = GetIdentificationUseCase(
            contractSignRepositoryMockk, identityInitializationRepositoryMockk
        ).execute(Unit).blockingGet().nextStep

        expectedNextStep shouldBe resultNextStep
    }

    "defaultToFallbackStep is null, fallback is null"{

        val expectedNextStep = "expectedNextStep"

        val expectedFallbackStep = null

        val identificationDtoMockk = mockk<IdentificationDto>() {
            every { nextStep } returns expectedNextStep
            every { fallbackStep } returns expectedFallbackStep
        }
        val contractSignRepositoryMockk = mockk<ContractSignRepository>() {
            every { getIdentification() } returns Single.just(identificationDtoMockk)
        }

        val partnerSettingsDtoMockk = mockk<PartnerSettingsDto>() {
            every { defaultToFallbackStep } returns null
        }

        val initializationDtoMockk = mockk<InitializationDto>() {
            every { partnerSettings } returns partnerSettingsDtoMockk
        }

        val identityInitializationRepositoryMockk =
            mockk<IdentityInitializationRepository>() {
                every { getInitializationDto() } returns initializationDtoMockk
            }

        val resultNextStep: String? = GetIdentificationUseCase(
            contractSignRepositoryMockk, identityInitializationRepositoryMockk
        ).execute(Unit).blockingGet().nextStep

        expectedNextStep shouldBe resultNextStep
    }
})