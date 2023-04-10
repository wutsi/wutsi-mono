package com.wutsi.application.web.endpoint

import com.wutsi.application.web.Page
import com.wutsi.application.web.model.PageModel
import com.wutsi.application.web.service.recaptcha.Recaptcha
import com.wutsi.application.web.util.ErrorCode
import com.wutsi.checkout.manager.dto.CreateOrderItemRequest
import com.wutsi.enums.OrderType
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/order")
class OrderController(
    private val httpRequest: HttpServletRequest,
    private val recaptcha: Recaptcha,
    private val messages: MessageSource,

    @Value("\${wutsi.application.google.recaptcha.site-key}") private val recaptchaSiteKey: String,
) : AbstractController() {
    @GetMapping
    fun index(
        @RequestParam(name = "p") productId: Long,
        @RequestParam(name = "q") quantity: Int,
        @RequestParam(name = "e", required = false) error: Long? = null,
        model: Model,
    ): String {
        val ofr = marketplaceManagerApi.getOffer(productId).offer
        val merchant = resolveCurrentMerchant(ofr.product.store.accountId)
        val offer = mapper.toOfferModel(ofr, merchant)
        val country = regulationEngine.country(merchant.country)

        val fmt = country.createMoneyFormat()
        val subTotal = ofr.product.price?.let {
            fmt.format(it * quantity)
        }

        val totalSavings = if (ofr.price.savings > 0) {
            fmt.format(ofr.price.savings * quantity)
        } else {
            null
        }
        val totalPrice = fmt.format(ofr.price.price * quantity)

        model.addAttribute("page", createPage())
        model.addAttribute("offer", offer)
        model.addAttribute("quantity", quantity)
        model.addAttribute("merchant", merchant)
        model.addAttribute("subTotal", subTotal)
        model.addAttribute("totalSavings", totalSavings)
        model.addAttribute("totalPrice", totalPrice)
        model.addAttribute("error", error?.let { toError(it) })

        return "order"
    }

    @PostMapping("/submit")
    fun submit(
        @ModelAttribute request: com.wutsi.application.web.dto.CreateOrderRequest,
    ): String {
        logger.add("request_business_id", request.businessId)
        logger.add("request_product_id", request.productId)
        logger.add("request_quantity", request.quantity)
        logger.add("request_notes", request.notes.take(10))
        logger.add("request_email", request.email)
        logger.add("request_display_name", request.displayName)

        // Recaptcha
        val recaptchaResponse = httpRequest.getParameter(Recaptcha.REQUEST_PARAMETER)
        logger.add("request_g-recaptcha-response", recaptchaResponse)
        if (!recaptcha.verify(recaptchaResponse)) {
            return "redirect:/order?p=${request.productId}&q=${request.quantity}&e=${ErrorCode.RECAPTCHA}"
        }

        // Order
        val ua = httpRequest.getHeader(HttpHeaders.USER_AGENT)
        val orderId = checkoutManagerApi.createOrder(
            request = com.wutsi.checkout.manager.dto.CreateOrderRequest(
                type = OrderType.SALES.name,
                deviceType = toDeviceType(ua)?.name,
                channelType = toChannelType(ua).name,
                businessId = request.businessId,
                customerName = request.displayName,
                customerEmail = request.email,
                notes = request.notes,
                items = listOf(
                    CreateOrderItemRequest(
                        productId = request.productId,
                        quantity = request.quantity,
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
        name = Page.ORDER,
        title = "Order",
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
