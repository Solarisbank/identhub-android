
import de.solarisbank.sdk.fourthline.streetNumber
import de.solarisbank.sdk.fourthline.streetSuffix
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class AddressValidationTest {

    @Test
    fun parseStreetNumberSuffix() {
        val result = TEST_ADDRESS_EXPANDED.streetSuffix()
        assertThat(result, equalTo("c"))
    }

    @Test
    fun parseStreetNumberSuffix2() {
        val result = TEST_ADDRESS.streetSuffix()
        assertThat(result, equalTo(null))
    }

    @Test
    fun parseStreetNumber() {
        val result = TEST_ADDRESS_EXPANDED.streetNumber()
        assertThat(result, `is`(76))
    }

    @Test
    fun parseStreetNumber2() {
        val result = TEST_ADDRESS.streetNumber()
        assertThat(result, `is`(76))
    }

    companion object {
        val TEST_ADDRESS_EXPANDED = "76c/5"
        val TEST_ADDRESS = "76"
    }
}