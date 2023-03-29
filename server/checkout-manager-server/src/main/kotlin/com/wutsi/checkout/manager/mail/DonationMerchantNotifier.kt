package com.wutsi.checkout.manager.mail

import com.wutsi.checkout.access.dto.Transaction
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.Party
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class DonationMerchantNotifier : AbstractDonationNotifier() {
    override fun createMessage(
        tx: Transaction,
        merchant: Account,
        type: MessagingType,
    ): Message? {
        val locale = Locale(merchant.language)
        return when (type) {
            MessagingType.PUSH_NOTIFICATION -> getDeviceToken(merchant)?.let {
                Message(
                    recipient = Party(
                        deviceToken = it,
                        displayName = merchant.displayName,
                    ),
                    body = getText("push-notification.donation-notify-merchant.body", locale = locale),
                )
            }
            else -> null
        }
    }
}
