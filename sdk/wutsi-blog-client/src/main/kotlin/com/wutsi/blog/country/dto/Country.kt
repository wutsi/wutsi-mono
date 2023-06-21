package com.wutsi.blog.country.dto

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class Country(
    val code: String,
    val currency: String,
    val currencySymbol: String,
    val numberFormat: String,
    val monetaryFormat: String,
    val dateFormat: String,
    val dateFormatShort: String,
    val timeFormat: String,
    val dateTimeFormat: String,
    val supportsDonation: Boolean,
    val languages: List<String>,
    val donationBaseAmount: Long,
) {
    val locale: String
        get() = languages[0] + "_$code"

    companion object {
        val CM = Country(
            code = "CM",
            currency = "XAF",
            currencySymbol = "FCFA",
            numberFormat = "#,###,##0",
            monetaryFormat = "#,###,##0 FCFA",
            dateFormat = "dd MMM yyy",
            dateFormatShort = "dd MMM",
            timeFormat = "HH:mm",
            dateTimeFormat = "dd MMM yyy, HH:mm",
            supportsDonation = true,
            languages = listOf("fr", "en"),
            donationBaseAmount = 500,
        )

        val all = listOf(CM)
    }

    fun createNumberFormat(): DecimalFormat {
        val fmt = DecimalFormat(numberFormat)
        fmt.decimalFormatSymbols = DecimalFormatSymbols(Locale(languages[0], code))
        return fmt
    }

    fun createMoneyFormat(): DecimalFormat {
        val fmt = DecimalFormat(monetaryFormat)
        fmt.decimalFormatSymbols = DecimalFormatSymbols(Locale(languages[0], code))
        return fmt
    }
}
