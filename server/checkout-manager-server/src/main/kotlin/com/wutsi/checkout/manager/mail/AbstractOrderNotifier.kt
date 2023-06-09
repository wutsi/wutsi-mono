package com.wutsi.checkout.manager.mail

import com.wutsi.checkout.access.dto.Order
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingType
import org.slf4j.LoggerFactory

abstract class AbstractOrderNotifier : AbstractNotifier() {
    protected abstract fun createMessage(
        order: Order,
        merchant: Account,
        type: MessagingType,
    ): Message?

    fun send(orderId: String) {
        // Order
        val order = checkoutAccessApi.getOrder(orderId).order
        logger.add("order_customer_name", order.customerName)
        logger.add("order_customer_email", order.customerEmail)
        logger.add("merchant_id", order.business.accountId)

        // Merchant
        val merchant = membershipAccessApi.getAccount(order.business.accountId).account

        // Send email
        createMessage(order, merchant, MessagingType.EMAIL)?.let {
            val messageId = sendEmail(message = debug(it))
            logger.add("message_id_email", messageId)
        }

        // Send push notification
        createMessage(order, merchant, MessagingType.PUSH_NOTIFICATION)?.let {
            try {
                val messageId = sendPushNotification(message = debug(it))
                logger.add("message_id_push", messageId)
            } catch (ex: Exception) {
                LoggerFactory.getLogger(javaClass).warn("Unable to send push notification", ex)
            }
        }
    }
}
