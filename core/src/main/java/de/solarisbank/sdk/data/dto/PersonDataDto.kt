package de.solarisbank.sdk.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class PersonDataDto(
    @Json(name = "first_name") var firstName: String?,
    @Json(name = "last_name") var lastName: String?,
    var address: Address?,
    var email: String?,
    @Json(name = "mobile_number") var mobileNumber: String?,
    var nationality: String?,
    @Json(name = "birth_date") var birthDate: String?,
    @Json(name = "place_of_birth") var birthPlace: String?,
    @Json(name = "person_uid") var personUid: String?,
    @Json(name = "supported_documents") var supportedDocuments: List<SupportedDocument?>?,
    var gender: String?
)

@JsonClass(generateAdapter = true)
data class SupportedDocument(
     var type: String?,
     @Json(name = "issuing_countries") var issuingCountries: List<String?>?
)

@JsonClass(generateAdapter = true)
data class Address(
    var street: String?,
    @Json(name = "street_number") var streetNumber: String?,
    var city: String?,
    var country: String?,
    @Json(name = "postal_code") var postalCode: String?
)