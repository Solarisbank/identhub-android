package de.solarisbank.sdk.fourthline
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AddressValidationTest : StringSpec ({

    val testAddressExpanded = "76c/5"
    val testAddress = "76"

    "parseStreetNumberSuffix" {
        val result = testAddressExpanded.streetSuffix()
        result shouldBe "c"
    }

    "parseStreetNumberSuffix2" {
        val result = testAddress.streetSuffix()
        result shouldBe null
    }

    "parseStreetNumber" {
        val result: Int? = testAddressExpanded.streetNumber()
        result shouldBe 76
    }

    "parseStreetNumber2" {
        val result: Int? = testAddress.streetNumber()
        result shouldBe 76
    }
})