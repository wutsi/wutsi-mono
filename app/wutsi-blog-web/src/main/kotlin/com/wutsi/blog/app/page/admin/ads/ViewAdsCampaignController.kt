package com.wutsi.blog.app.page.admin.ads

import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.UpdateAdsAttributeCommand
import com.wutsi.blog.app.model.AdsModel
import com.wutsi.blog.app.page.AbstractStoreController
import com.wutsi.blog.app.service.AdsService
import com.wutsi.blog.app.service.CountryService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.util.PageName
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.LoggerFactory
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

@Controller
@RequestMapping
class ViewAdsCampaignController(
    private val service: AdsService,
    private val countryService: CountryService,
    requestContext: RequestContext,
) : AbstractStoreController(requestContext) {
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
            return "redirect:/me/ads/campaigns/$id"
        } catch (ex: Exception) {
            LOGGER.error("Unexpected error", ex)
            val error = toErrorKey(ex)
            return "redirect:/me/ads/campaigns/$id?error=$error"
        }
    }
}
