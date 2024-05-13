package com.wutsi.blog.app.page.admin.ads

import com.wutsi.blog.app.form.PayForm
import com.wutsi.blog.app.mapper.CountryMapper
import com.wutsi.blog.app.model.TransactionModel
import com.wutsi.blog.app.page.AbstractPageController
import com.wutsi.blog.app.service.AdsService
import com.wutsi.blog.app.service.RequestContext
import com.wutsi.blog.app.service.TransactionService
import com.wutsi.blog.app.service.UserService
import com.wutsi.blog.app.util.PageName
import com.wutsi.blog.country.dto.Country
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@Controller
class PayAdsController(
    private val userService: UserService,
    private val adsService: AdsService,
    private val transactionService: TransactionService,
    private val logger: KVLogger,
    private val countryMapper: CountryMapper,
    @Value("\${wutsi.paypal.client-id}") private val paypalClientId: String,

    requestContext: RequestContext,
) : AbstractPageController(requestContext) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PayAdsController::class.java)
    }

    override fun pageName() = PageName.ADS_PAY

    @GetMapping("/me/ads/pay")
    fun index(
        @RequestParam("ads-id") adsId: String,
        @RequestParam(required = false) error: String? = null,
        @RequestParam(required = false, name = "t") transactionId: String? = null,
        model: Model,
    ): String {
        val ads = adsService.get(adsId)
        val blog = userService.get(ads.userId)
        val user = requestContext.currentUser()
        val tx = transactionId?.let { resolveTransaction(transactionId) }

        val form = PayForm(
            adsId = adsId,
            amount = ads.budget.value,
            email = tx?.email ?: user?.email ?: "",
            fullName = tx?.paymentMethodOwner ?: user?.fullName ?: "",
            idempotencyKey = UUID.randomUUID().toString(),
            error = error?.let { requestContext.getMessage(error) },
            country = blog.country ?: "",
        )

        model.addAttribute("form", form)
        model.addAttribute("blog", blog)
        model.addAttribute("ads", ads)
        model.addAttribute("idempotencyKey", UUID.randomUUID().toString())
        model.addAttribute("paypalClientId", paypalClientId)
        model.addAttribute("countryCodeCSV", Country.all.map { it.code }.joinToString(separator = ","))
        loadPaymentMethodType(model)

        return "admin/ads/pay"
    }

    @PostMapping("/me/ads/pay/submit")
    fun submit(
        @ModelAttribute form: PayForm,
        model: Model,
    ): String {
        try {
            val transactionId = transactionService.pay(form)
            logger.add("transaction_id", transactionId)
            return "redirect:/processing?id=$transactionId"
        } catch (ex: Exception) {
            LOGGER.error("Purchase failed", ex)
            return "redirect:/me/ads/pay?ads-id=${form.adsId}&error=" + toErrorKey(ex)
        }
    }

    private fun resolveTransaction(id: String): TransactionModel? =
        try {
            transactionService.get(id, false)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to resolve transaction#$id", ex)
            null
        }

    private fun loadPaymentMethodType(model: Model) {
        val paymentMethodTypes = Country.all
            .map { country -> countryMapper.toCountryModel(country) }
            .flatMap { country -> country.paymentProviderTypes }
            .toSet()
        model.addAttribute("paymentProviderTypes", paymentMethodTypes)
    }
}
