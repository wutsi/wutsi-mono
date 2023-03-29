package com.wutsi.checkout.manager.mail

import com.wutsi.checkout.access.dto.Order
import com.wutsi.checkout.manager.mail.model.OrderModel
import com.wutsi.enums.ProductType
import com.wutsi.mail.MailFilterSet
import com.wutsi.marketplace.access.dto.SearchProductRequest
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.Party
import com.wutsi.regulation.Country
import com.wutsi.regulation.RegulationEngine
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale

@Service
class OrderCustomerNotifier(
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
            MessagingType.EMAIL -> Message(
                recipient = Party(
                    email = order.customerEmail,
                    displayName = order.customerName,
                ),
                subject = getText(
                    "email.notify-customer.subject",
                    arrayOf(merchant.displayName.uppercase()),
                    locale = locale,
                ),
                body = generateBody(order, locale, merchant),
                mimeType = "text/html;charset=UTF-8",
            )
            else -> null
        }
    }

    private fun generateBody(order: Order, locale: Locale, merchant: Account): String {
        val ctx = Context(locale)

        val store = merchant.storeId?.let {
            marketplaceAccessApi.getStore(it).store
        }
        store?.let {
            ctx.setVariable("store", mapper.toStoreModel(it))
        }

        val country = regulationEngine.country(order.business.country)
        val mailContext = createMailContext(merchant)
        ctx.setVariable("order", toOrderModel(order, country))
        ctx.setVariable("merchant", mailContext.merchant)

        val body = templateEngine.process("order-customer.html", ctx)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    private fun toOrderModel(order: Order, country: Country): OrderModel {
        val model = mapper.toOrderModel(order, country)
        attachEvents(model, country)
        attachFiles(model)
        return model
    }

    private fun attachEvents(order: OrderModel, country: Country) {
        val productIds = order.items
            .filter { it.productType == ProductType.EVENT.name }
            .map { it.productId }
        if (productIds.isEmpty()) {
            return
        }

        val products = marketplaceAccessApi.searchProduct(
            request = SearchProductRequest(
                productIds = productIds,
                limit = productIds.size,
            ),
        ).products.associateBy { it.id }
        order.items
            .filter { it.productType == ProductType.EVENT.name }
            .forEach {
                it.event = products[it.productId]?.event?.let { mapper.toEventModel(it, country) }
            }
    }

    private fun attachFiles(order: OrderModel) {
        order.items
            .filter { it.productType == ProductType.DIGITAL_DOWNLOAD.name }
            .forEach {
                val product = marketplaceAccessApi.getProduct(it.productId).product
                val item = it
                it.files = product.files.map { mapper.toFileModel(it, order, item) }
            }
    }
}
