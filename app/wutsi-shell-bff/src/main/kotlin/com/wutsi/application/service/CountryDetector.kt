package com.wutsi.application.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.springframework.stereotype.Service

@Service
class CountryDetector {
    companion object {
        // See https://en.wikipedia.org/wiki/Telephone_numbers_in_Canada
        private val CANADA_PREFIX: List<String> = listOf(
            403, 587, 780, 825,
            236, 250, 604, 672, 778,
            204, 431,
            506,
            709,
            782, 902,
            226, 249, 289, 343, 365, 416, 437, 519, 548, 613, 647, 705, 807, 905,
            367, 418, 438, 450, 514, 579, 581, 819, 873,
            306, 639,
            867,
        ).map { "+1$it" }
    }

    fun detect(phoneNumber: String): String {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val phone = phoneNumberUtil.parse(phoneNumber, "")
        val country = phoneNumberUtil.getRegionCodeForCountryCode(phone.countryCode)
        if (country == "US") {
            if (isFromCanada(phoneNumber)) {
                return "CA"
            }
        }
        return country
    }

    private fun isFromCanada(phoneNumber: String) =
        CANADA_PREFIX.contains(phoneNumber.substring(0, 5))
}
