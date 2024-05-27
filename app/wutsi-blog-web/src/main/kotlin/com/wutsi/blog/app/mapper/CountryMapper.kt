package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.CountryModel
import com.wutsi.blog.app.model.PaymentProviderTypeModel
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.transaction.dto.PaymentProviderType
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class CountryMapper(
    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
) {
    fun toCountryModel(country: Country): CountryModel {
        val locale = Locale(
            LocaleContextHolder.getLocale().language,
            country.code,
        )

        return CountryModel(
            code = country.code,
            name = locale.displayCountry,
            currencyCode = country.currency,
            currencySymbol = country.currencySymbol,
            currencyDisplayName = country.currencyName,
            flagUrl = "https://flagcdn.com/w20/${country.code.lowercase()}.png",
            paymentProviderTypes = country.paymentProviderTypes.map { toPaymentProviderTypeModel(it) },
            monetaryFormat = country.monetaryFormat,
            internationalCurrency = country.internationalCurrency,
            minDailyAdsBudget = country.minDailyAdsBudget
        )
    }

    fun toCountryModel(countryCode: String): CountryModel {
        val locale = Locale(
            LocaleContextHolder.getLocale().language,
            countryCode,
        )

        return CountryModel(
            code = countryCode,
            name = locale.displayCountry,
            flagUrl = "https://flagcdn.com/w20/${countryCode.lowercase()}.png",
        )
    }

    fun toPaymentProviderTypeModel(obj: PaymentProviderType) = PaymentProviderTypeModel(
        type = obj,
        logoUrl = "$assetUrl/assets/wutsi/img/payment/${obj.name.lowercase()}.png",
    )
}
