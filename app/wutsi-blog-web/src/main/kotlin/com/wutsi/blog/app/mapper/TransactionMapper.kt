package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.CountryModel
import com.wutsi.blog.country.dto.Country
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class CountryMapper {
    fun toCountryModel(country: Country): CountryModel {
        val locale = Locale(
            LocaleContextHolder.getLocale().language,
            country.code,
        )

        return CountryModel(
            code = country.code,
            name = locale.displayCountry,
            currencyCode = country.currency,
            currencyDisplayName = country.currencyName,
        )
    }
}
