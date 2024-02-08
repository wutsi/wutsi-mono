package com.wutsi.blog.country.dto

import com.wutsi.blog.transaction.dto.PaymentProviderType
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class Country(
    val code: String,
    val currency: String,
    val currencyName: String,
    val currencySymbol: String,
    val numberFormat: String,
    val monetaryFormat: String,
    val dateFormat: String,
    val dateFormatShort: String,
    val timeFormat: String,
    val dateTimeFormat: String,
    val languages: List<String>,
    val defaultDonation: Long,
    val defaultDonationAmounts: Array<Long>,
    val paymentProviderTypes: List<PaymentProviderType>,
    val phoneNumberCode: Int,
    val minCashoutAmount: Long,
    val wppEarningThreshold: Long,
    val internationalCurrency: String,
    val internationalMonetaryFormat: String,
) {
    val locale: String
        get() = languages[0] + "_$code"

    companion object {
        val CM = Country(
            code = "CM",
            currency = "XAF",
            currencyName = "Franc CFA",
            currencySymbol = "FCFA",
            numberFormat = "#,###,##0",
            monetaryFormat = "#,###,##0 FCFA",
            dateFormat = "dd MMM yyy",
            dateFormatShort = "dd MMM",
            timeFormat = "HH:mm",
            dateTimeFormat = "dd MMM yyy, HH:mm",
            languages = listOf("fr", "en"),
            wppEarningThreshold = 1000,
            defaultDonation = 500L,
            minCashoutAmount = 700L,
            defaultDonationAmounts = arrayOf(500L, 1000L, 2000L, 5000L),
            paymentProviderTypes = listOf(PaymentProviderType.MTN, PaymentProviderType.ORANGE),
            phoneNumberCode = 237,
            internationalCurrency = "EUR",
            internationalMonetaryFormat = "€ #,###,##0"
        )
        val CI = Country(
            code = "CI",
            currency = "XOF",
            currencyName = "Franc CFA",
            currencySymbol = "FCFA",
            numberFormat = "#,###,##0",
            monetaryFormat = "#,###,##0 FCFA",
            dateFormat = "dd MMM yyy",
            dateFormatShort = "dd MMM",
            timeFormat = "HH:mm",
            dateTimeFormat = "dd MMM yyy, HH:mm",
            languages = listOf("fr"),
            wppEarningThreshold = 1000,
            defaultDonation = 500L,
            minCashoutAmount = 700L,
            defaultDonationAmounts = arrayOf(500L, 1000L, 2000L, 5000L),
            paymentProviderTypes = listOf(PaymentProviderType.MTN, PaymentProviderType.ORANGE),
            phoneNumberCode = 225,
            internationalCurrency = "EUR",
            internationalMonetaryFormat = "€ #,###,##0"
        )
        val SN = Country(
            code = "SN",
            currency = "XOF",
            currencyName = "Franc CFA",
            currencySymbol = "FCFA",
            numberFormat = "#,###,##0",
            monetaryFormat = "#,###,##0 FCFA",
            dateFormat = "dd MMM yyy",
            dateFormatShort = "dd MMM",
            timeFormat = "HH:mm",
            dateTimeFormat = "dd MMM yyy, HH:mm",
            languages = listOf("fr"),
            wppEarningThreshold = 1000,
            defaultDonation = 500L,
            minCashoutAmount = 700L,
            defaultDonationAmounts = arrayOf(500L, 1000L, 2000L, 5000L),
            paymentProviderTypes = listOf(PaymentProviderType.ORANGE),
            phoneNumberCode = 221,
            internationalCurrency = "EUR",
            internationalMonetaryFormat = "€ #,###,##0"
        )
        val BF = Country(
            code = "BF",
            currency = "XOF",
            currencyName = "Franc CFA",
            currencySymbol = "FCFA",
            numberFormat = "#,###,##0",
            monetaryFormat = "#,###,##0 FCFA",
            dateFormat = "dd MMM yyy",
            dateFormatShort = "dd MMM",
            timeFormat = "HH:mm",
            dateTimeFormat = "dd MMM yyy, HH:mm",
            languages = listOf("fr"),
            wppEarningThreshold = 1000,
            defaultDonation = 500L,
            minCashoutAmount = 700L,
            defaultDonationAmounts = arrayOf(500L, 1000L, 2000L, 5000L),
            paymentProviderTypes = listOf(PaymentProviderType.ORANGE),
            phoneNumberCode = 226,
            internationalCurrency = "EUR",
            internationalMonetaryFormat = "€ #,###,##0"
        )

        val all = listOf(CM, CI, SN, BF)

        fun fromPhoneNumber(phone: String): Country? {
            val xphone = if (phone.startsWith("+")) phone.substring(1) else phone
            return all.find { country -> xphone.startsWith("${country.phoneNumberCode}") }
        }
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
