package com.wutsi.blog.app.page.settings.monetization

import com.wutsi.blog.app.AbstractPageController
import com.wutsi.blog.app.form.CreateWalletForm
import com.wutsi.blog.app.mapper.CountryMapper
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.WalletService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/me/settings/monetization/review")
class MonetizationReviewController(
    private val service: WalletService,
    private val countryMapper: CountryMapper,
    private val logger: KVLogger,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MonetizationController::class.java)
    }

    override fun pageName() = PageName.SETTINGS_MONETIZATION_REVIEW

    @GetMapping
    fun index(
        model: Model,
        @RequestParam code: String,
        @RequestParam(required = false) error: String? = null,
    ): String {
        val country = Country.all.map { countryMapper.toCountryModel(it) }.find { it.code == code }

        model.addAttribute("form", CreateWalletForm(code))
        model.addAttribute("country", country)
        if (error != null) {
            model.addAttribute("error", requestContext.getMessage(error))
        }

        return "settings/monetization/review"
    }

    @PostMapping
    fun submit(@ModelAttribute form: CreateWalletForm): String =
        try {
            val walletId = service.create(form)
            logger.add("wallet_id", walletId)
            "redirect:/me/settings/monetization/success"
        } catch (ex: Exception) {
            LOGGER.error("Unable to enable monetization", ex)
            "redirect:/me/settings/monetization/review?error=error.unexpected"
        }
}
