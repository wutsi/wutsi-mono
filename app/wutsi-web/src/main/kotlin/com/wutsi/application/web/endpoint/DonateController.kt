package com.wutsi.application.web.endpoint

import com.wutsi.application.web.Page
import com.wutsi.application.web.model.PageModel
import com.wutsi.application.web.service.recaptcha.Recaptcha
import com.wutsi.application.web.util.ErrorCode
import com.wutsi.checkout.manager.dto.CreateOrderItemRequest
import com.wutsi.enums.OrderType
import com.wutsi.membership.manager.dto.Member
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URLEncoder
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping
class DonateController(
    private val httpRequest: HttpServletRequest,
    private val recaptcha: Recaptcha,
    private val messages: MessageSource,

    @Value("\${wutsi.application.google.recaptcha.site-key}") private val recaptchaSiteKey: String,
) : AbstractController() {
    @GetMapping("/u/{id}/donate")
    fun index(
        @PathVariable id: Long,
        @RequestParam(name = "dn", required = false) displayName: String? = null,
        @RequestParam(name = "n", required = false) notes: String? = null,
        @RequestParam(name = "e", required = false) error: Long? = null,
        model: Model,
    ): String =
        render(
            merchant = resolveCurrentMerchant(id),
            displayName = displayName,
            notes = notes,
            error = error,
            model = model,
        )

    @GetMapping("/@{name}/donate")
    fun index(
        @PathVariable name: String,
        @RequestParam(name = "dn", required = false) displayName: String? = null,
        @RequestParam(name = "n", required = false) notes: String? = null,
        @RequestParam(name = "e", required = false) error: Long? = null,
        model: Model,
    ): String =
        render(
            merchant = resolveCurrentMerchant(name),
            displayName = displayName,
            notes = notes,
            error = error,
            model = model,
        )

    private fun render(
        merchant: Member,
        displayName: String? = null,
        notes: String? = null,
        error: Long? = null,
        model: Model,
    ): String {
        model.addAttribute("page", createPage())
        model.addAttribute("businessId", merchant.businessId)
        model.addAttribute("displayName", displayName)
        model.addAttribute("notes", notes)
        model.addAttribute("merchant", mapper.toMemberModel(merchant))
        model.addAttribute("error", error?.let { toError(it) })

        return "donate"
    }

    @PostMapping("/donate/submit")
    fun submit(
        @ModelAttribute request: com.wutsi.application.web.dto.CreateDonationRequest,
    ): String {
        logger.add("request_business_id", request.businessId)
        logger.add("request_notes", request.notes.take(10))
        logger.add("request_email", request.email)
        logger.add("request_display_name", request.displayName)

        // Recaptcha
        val business = checkoutManagerApi.getBusiness(request.businessId).business
        val recaptchaResponse = httpRequest.getParameter(Recaptcha.REQUEST_PARAMETER)
        logger.add("request_g-recaptcha-response", recaptchaResponse)
        if (!recaptcha.verify(recaptchaResponse)) {
            return "redirect:/u/${business.accountId}/donate?&e=${ErrorCode.RECAPTCHA}" +
                "dn=" + URLEncoder.encode(request.displayName, "utf-8") +
                "&n=" + URLEncoder.encode(request.notes, "utf-8")
        }

        // Order
        val ua = httpRequest.getHeader(HttpHeaders.USER_AGENT)
        val orderId = checkoutManagerApi.createOrder(
            request = com.wutsi.checkout.manager.dto.CreateOrderRequest(
                type = OrderType.DONATION.name,
                deviceType = toDeviceType(ua)?.name,
                channelType = toChannelType(ua).name,
                businessId = request.businessId,
                customerName = request.displayName,
                customerEmail = request.email,
                notes = request.notes,
                items = listOf(
                    CreateOrderItemRequest(
                        productId = -1,
                        quantity = 1,
                    ),
                ),
            ),
        ).orderId
        val idempotencyKey = UUID.randomUUID().toString()
        logger.add("order_id", orderId)
        logger.add("idempotency_key", idempotencyKey)

        return "redirect:/payment?o=$orderId&i=$idempotencyKey"
    }

    private fun createPage() = PageModel(
        name = Page.DONATE,
        title = "Donate",
        robots = "noindex",
        recaptchaSiteKey = recaptchaSiteKey,
    )

    private fun toError(error: Long): String? = when (error) {
        ErrorCode.RECAPTCHA -> {
            messages.getMessage(
                "error-message.recaptcha-error",
                emptyArray(),
                LocaleContextHolder.getLocale(),
            )
        }
        else -> messages.getMessage("error-message.unexpected", emptyArray(), LocaleContextHolder.getLocale())
    }
}
