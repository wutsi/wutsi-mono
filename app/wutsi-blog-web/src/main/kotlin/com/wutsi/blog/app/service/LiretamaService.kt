package com.wutsi.blog.app.service

import com.wutsi.blog.app.mapper.CountryMapper
import com.wutsi.blog.app.model.CountryModel
import org.springframework.stereotype.Component

@Component
class LiretamaService(
    private val countryMapper: CountryMapper,
) {
    private val countryCodes = listOf(
        "BJ", // Benin
        "BR", // Burkina Fasso
        "CM", // Cameroon
        "CG", // Congo
        "CI", // Côte d'Ivoire
        "GA", // Gabon
        "GH", // Ghana
        "GN", // Guinée
        "KE", // Kenya
        "LR", // Libéria
        "MG", // Madadascar
        "MW", // Malawi
        "ML", // Mali
        "NE", // Niger
        "UG", // Ouganda
        "CD", // RD Congo
        "RW", // Rwanda
        "SN", // Sénégal
        "TZ", // Tanzanie
        "TG", // Togo
        "ZM", // Zambia
    )

    fun getSupportedCountries(): List<CountryModel> =
        countryCodes.map { code -> countryMapper.toCountryModel(code) }
}
