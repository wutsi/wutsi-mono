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
    val donationBaseAmount: Long,
    val minCashoutAmount: Long,
    val paymentProviderTypes: List<PaymentProviderType>,
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
            donationBaseAmount = 1000,
            minCashoutAmount = 10000,
            paymentProviderTypes = listOf(PaymentProviderType.MTN, PaymentProviderType.ORANGE),
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
            donationBaseAmount = 1000,
            minCashoutAmount = 10000,
            paymentProviderTypes = listOf(PaymentProviderType.MTN, PaymentProviderType.ORANGE),
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
            donationBaseAmount = 1000,
            minCashoutAmount = 10000,
            paymentProviderTypes = listOf(PaymentProviderType.MTN, PaymentProviderType.ORANGE),
        )

        val all = listOf(CM, CI, SN)
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
