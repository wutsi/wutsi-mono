package com.wutsi.blog.app.page.settings.monetization

import com.wutsi.blog.app.mapper.CountryMapper
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/me/settings/monetization/country")
class MonetizationCountryController(
    private val countryMapper: CountryMapper,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    override fun pageName() = PageName.SETTINGS_MONETIZATION_COUNTRY

    @GetMapping
    fun index(model: Model): String {
        val countries = Country.all.map { countryMapper.toCountryModel(it) }.sortedBy { it.name }

        model.addAttribute("countries", countries)

        return "settings/monetization/country"
    }
}
