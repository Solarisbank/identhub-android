package de.solarisbank.sdk.fourthline
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AddressValidationTest : StringSpec ({

    val TEST_ADDRESS_EXPANDED = "76c/5"
    val TEST_ADDRESS = "76"

    "parseStreetNumberSuffix" {
        val result = TEST_ADDRESS_EXPANDED.streetSuffix()
        result shouldBe "c"
    }

    "parseStreetNumberSuffix2" {
        val result = TEST_ADDRESS.streetSuffix()
        result shouldBe null
    }

    "parseStreetNumber" {
        val result: Int? = TEST_ADDRESS_EXPANDED.streetNumber()
        result shouldBe 76
    }

    "parseStreetNumber2" {
        val result: Int? = TEST_ADDRESS.streetNumber()
        result shouldBe 76
    }
})