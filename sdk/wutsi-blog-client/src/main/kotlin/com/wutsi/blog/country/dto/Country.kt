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
    val phoneNumberPrefixes: List<PhoneNumberPrefix>,
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
            defaultDonation = 2000,
            defaultDonationAmounts = arrayOf(1000L, 2000L, 5000L, 10000L),
            paymentProviderTypes = listOf(PaymentProviderType.MTN, PaymentProviderType.ORANGE),
            phoneNumberPrefixes = listOf(
                PhoneNumberPrefix(PaymentProviderType.MTN, "+237650"),
                PhoneNumberPrefix(PaymentProviderType.MTN, "+237670"),
                PhoneNumberPrefix(PaymentProviderType.MTN, "+237680"),

                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+237655"),
                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+237656"),
                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+237657"),
                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+237658"),
                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+237659"),
                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+23769"),
            ),
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
            defaultDonation = 2000,
            defaultDonationAmounts = arrayOf(1000L, 2000L, 5000L, 10000L),
            paymentProviderTypes = listOf(PaymentProviderType.MTN, PaymentProviderType.ORANGE),
            phoneNumberPrefixes = listOf(
                PhoneNumberPrefix(PaymentProviderType.MTN, "+22505"),
                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+22507"),
            ),
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
            defaultDonation = 2000,
            defaultDonationAmounts = arrayOf(1000L, 2000L, 5000L, 10000L),
            paymentProviderTypes = listOf(PaymentProviderType.ORANGE),
            phoneNumberPrefixes = listOf(
                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+22177"),
                PhoneNumberPrefix(PaymentProviderType.ORANGE, "+22178"),
            ),
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
