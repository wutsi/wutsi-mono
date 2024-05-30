package com.wutsi.blog.app.service

import com.wutsi.blog.app.mapper.CountryMapper
import com.wutsi.blog.app.model.CountryModel
import com.wutsi.blog.app.model.ProductModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class LiretamaService(
    @Value("\${wutsi.liretama.affiliate-id}") private val affiliateId: String,
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

    fun isLiretamaProductURL(url: String): Boolean =
        url.lowercase().startsWith("https://www.liretama.com/livres/")

    fun toUrl(product: ProductModel) =
        if (product.liretamaUrl.isNullOrEmpty()) {
            "/product/${product.id}"
        } else {
            toProductUrl(product.liretamaUrl)
        }

    fun toProductUrl(url: String): String =
        if (isLiretamaProductURL(url)) {
            sanitizeUrl(url) + "?pid=$affiliateId"
        } else {
            url
        }

    private fun sanitizeUrl(url: String): String {
        val i = url.indexOf("?")
        return if (i > 0) {
            url.substring(0, i)
        } else {
            url
        }
    }
}
