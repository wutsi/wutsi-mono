package com.wutsi.blog.app.mapper

import com.wutsi.blog.app.model.CountryModel
import com.wutsi.blog.app.model.UserModel
import com.wutsi.blog.country.dto.Country
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class CountryMapper {
    fun toCountryModel(country: Country, user: UserModel?): CountryModel {
        val locale = Locale(
            user?.let { user.language } ?: "en",
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
