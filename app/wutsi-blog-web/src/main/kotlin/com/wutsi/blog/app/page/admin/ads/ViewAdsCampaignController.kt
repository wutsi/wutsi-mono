package com.wutsi.blog.app.page.admin.ads

import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import com.wutsi.blog.app.model.AdsModel
import com.wutsi.blog.app.model.MoneyModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.AdsService
import com.wutsi.blog.app.service.CountryService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.util.PageName
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Controller
@RequestMapping
class ViewAdsCampaignController(
    private val service: AdsService,
    private val countryService: CountryService,
    private val transactionService: TransactionService,
    requestContext: RequestContext,

    @Value("\${wutsi.toggles.ads-payment}") private val adsPaymentEnabled: Boolean,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ViewAdsCampaignController::class.java)
    }

    override fun pageName() = PageName.ADS_CAMPAIGNS_VIEW

    @GetMapping("/me/ads/campaigns/{id}")
    fun index(
        @PathVariable id: String,
        @RequestParam(required = false) error: String? = null,
        model: Model,
    ): String {
        val ads = service.get(id)

        model.addAttribute("types", AdsType.entries.filter { item -> item != AdsType.UNKNOWN })
        model.addAttribute("ads", ads)
        model.addAttribute("submitUrl", "/me/ads/campaigns/$id")

        val country = countryService.get(AdsModel.DEFAULT_COUNTRY_CODE)!!
        model.addAttribute("minDailyBudget", country.minDailyAdsBudget)
        model.addAttribute("country", country)

        val yesterday = DateUtils.addDays(Date(), -1)
        model.addAttribute("minDate", SimpleDateFormat("yyyy-MM-dd").format(yesterday))

        val locale = LocaleContextHolder.getLocale()
        model.addAttribute("locale", locale)

        val languages = Locale.getISOLanguages()
            .map { lang -> Locale(lang) }
            .toSet()
            .sortedBy { it.getDisplayLanguage(locale) }
        model.addAttribute("languages", languages)

        val countries = Locale.getISOCountries()
            .map { Locale(locale.language, it) }
            .sortedBy { it.getDisplayCountry(locale) }
        model.addAttribute("countries", countries)

        if (error != null) {
            model.addAttribute(
                "error",
                requestContext.getMessage(
                    error,
                    "error.unexpected",
                    emptyArray(),
                    LocaleContextHolder.getLocale()
                )
            )
        }

        if (ads.transactionId != null) {
            model.addAttribute(
                "tx",
                transactionService.get(ads.transactionId, false)
            )
        }

        return "admin/ads/view"
    }

    @ResponseBody
    @PostMapping("/me/ads/campaigns/{id}", produces = ["application/json"], consumes = ["application/json"])
    fun submit(@PathVariable id: String, @RequestBody form: UpdateAdsAttributeCommand): Map<String, String> {
        service.updateAttribute(id, form)
        return emptyMap()
    }

    @GetMapping("/me/ads/campaigns/{id}/publish")
    fun publish(@PathVariable id: String, model: Model): String {
        try {
            service.publish(id)
            return if (adsPaymentEnabled) {
                "redirect:/me/ads/pay?ads-id=$id"
            } else {
                "redirect:/me/ads/campaigns/$id"
            }
        } catch (ex: Exception) {
            LOGGER.error("Unexpected error", ex)
            val error = toErrorKey(ex)
            return "redirect:/me/ads/campaigns/$id?error=$error"
        }
    }

    @ResponseBody
    @GetMapping("/me/ads/campaigns/{id}/budget")
    fun budget(@PathVariable id: String): Map<String, MoneyModel> {
        val ads = service.get(id)
        return mapOf(
            "budget" to ads.budget,
            "dailyBudget" to ads.dailyBudget,
        )
    }
}
