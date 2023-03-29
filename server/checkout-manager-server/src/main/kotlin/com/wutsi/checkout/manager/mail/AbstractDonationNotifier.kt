package com.wutsi.checkout.manager.mail

import com.wutsi.checkout.access.dto.Transaction
import com.wutsi.enums.TransactionType
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingType
import org.slf4j.LoggerFactory

abstract class AbstractDonationNotifier : AbstractNotifier() {
    protected abstract fun createMessage(
        tx: Transaction,
        merchant: Account,
        type: MessagingType,
    ): Message?

    fun send(transactionId: String) {
        // Order
        val tx = checkoutAccessApi.getTransaction(transactionId).transaction
        if (tx.type != TransactionType.DONATION.name) {
            return
        }

        logger.add("donator_name", tx.paymentMethod.ownerName)
        logger.add("donator_email", tx.email)
        logger.add("merchant_id", tx.business.accountId)

        // Merchant
        val merchant = membershipAccessApi.getAccount(tx.business.accountId).account

        // Send email
        createMessage(tx, merchant, MessagingType.EMAIL)?.let {
            val messageId = sendEmail(message = debug(it))
            logger.add("message_id_email", messageId)
        }

        // Send push notification
        createMessage(tx, merchant, MessagingType.PUSH_NOTIFICATION)?.let {
            try {
                val messageId = sendPushNotification(message = debug(it))
                logger.add("message_id_push", messageId)
            } catch (ex: Exception) {
                LoggerFactory.getLogger(javaClass).warn("Unable to send push notification", ex)
            }
        }
    }
}
