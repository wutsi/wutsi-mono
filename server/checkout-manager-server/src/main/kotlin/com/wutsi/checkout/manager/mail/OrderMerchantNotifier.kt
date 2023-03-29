package com.wutsi.checkout.manager.mail

import com.wutsi.checkout.access.dto.Order
import com.wutsi.mail.MailFilterSet
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.Party
import com.wutsi.regulation.RegulationEngine
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale

@Service
class OrderMerchantNotifier(
    private val mapper: Mapper,
    private val templateEngine: TemplateEngine,
    private val regulationEngine: RegulationEngine,
    private val mailFilterSet: MailFilterSet,
) : AbstractOrderNotifier() {
    override fun createMessage(
        order: Order,
        merchant: Account,
        type: MessagingType,
    ): Message? {
        val locale = Locale(merchant.language)
        return when (type) {
            MessagingType.EMAIL -> merchant.email?.let {
                Message(
                    recipient = Party(
                        email = it,
                        displayName = merchant.displayName,
                    ),
                    subject = getText("email.notify-merchant.subject", locale = locale),
                    body = generateBody(order, merchant, locale, "wutsi"),
                    mimeType = "text/html;charset=UTF-8",
                )
            }
            MessagingType.PUSH_NOTIFICATION -> getDeviceToken(merchant)?.let {
                Message(
                    recipient = Party(
                        deviceToken = it,
                        displayName = merchant.displayName,
                    ),
                    body = getText("push-notification.notify-merchant.body", locale = locale),
                )
            }
            else -> null
        }
    }

    private fun generateBody(order: Order, merchant: Account, locale: Locale, template: String? = null): String {
        val ctx = Context(locale)
        val country = regulationEngine.country(order.business.country)
        val mailContext = createMailContext(merchant, template)
        ctx.setVariable("order", mapper.toOrderModel(order, country))
        ctx.setVariable("merchant", mailContext.merchant)

        val body = templateEngine.process("order-merchant.html", ctx)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }
}
