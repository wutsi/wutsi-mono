package com.wutsi.checkout.manager.mail

import com.wutsi.checkout.access.CheckoutAccessApi
import com.wutsi.mail.MailContext
import com.wutsi.mail.Merchant
import com.wutsi.marketplace.access.MarketplaceAccessApi
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import java.util.Locale

abstract class AbstractNotifier {
    @Value("\${wutsi.application.asset-url}")
    private lateinit var assetUrl: String

    @Value("\${wutsi.application.webapp-url}")
    private lateinit var webappUrl: String

    @Value("\${wutsi.application.notification.debug}")
    private lateinit var debugNodifications: String

    @Autowired
    private lateinit var messagingServiceProvider: MessagingServiceProvider

    @Autowired
    protected lateinit var logger: KVLogger

    @Autowired
    private lateinit var messages: MessageSource

    @Autowired
    protected lateinit var membershipAccessApi: MembershipAccessApi

    @Autowired
    protected lateinit var checkoutAccessApi: CheckoutAccessApi

    @Autowired
    protected lateinit var marketplaceAccessApi: MarketplaceAccessApi

    protected fun getText(
        key: String,
        args: Array<Any> = emptyArray(),
        locale: Locale = LocaleContextHolder.getLocale(),
    ): String =
        messages.getMessage(key, args, locale)

    protected fun debug(message: Message): Message {
        if (debugNodifications.toBoolean()) {
            val logger = LoggerFactory.getLogger(javaClass)
            logger.info("Recipient Address: ${message.recipient.displayName}< ${message.recipient.email}>")
            message.recipient.deviceToken?.let {
                logger.info("Recipient Device: $it")
            }
            message.subject?.let {
                logger.info("Subject: $it")
            }
            logger.info("\n${message.body}\n")
        }
        return message
    }

    protected fun createMailContext(merchant: Account, template: String? = null) = MailContext(
        assetUrl = assetUrl,
        merchant = Merchant(
            url = if (merchant.name == null) "$webappUrl/u/${merchant.id}" else "$webappUrl/@${merchant.name}",
            name = merchant.displayName.uppercase(),
            logoUrl = merchant.pictureUrl,
            category = merchant.category?.title,
            location = merchant.city?.longName,
            phoneNumber = merchant.phone.number,
            whatsapp = merchant.whatsapp,
            websiteUrl = merchant.website,
            twitterId = merchant.twitterId,
            facebookId = merchant.facebookId,
            instagramId = merchant.instagramId,
            youtubeId = merchant.youtubeId,
            country = merchant.country,
        ),
        template = template,
    )

    protected fun sendEmail(message: Message): String? {
        if (message.recipient.email.isNullOrEmpty()) {
            return null
        }
        val sender = messagingServiceProvider.get(MessagingType.EMAIL)
        return sender.send(message)
    }

    protected fun sendPushNotification(message: Message): String? {
        if (message.recipient.deviceToken.isNullOrEmpty()) {
            return null
        }
        val sender = messagingServiceProvider.get(MessagingType.PUSH_NOTIFICATION)
        return sender.send(message)
    }

    protected fun getDeviceToken(merchant: Account): String? =
        try {
            membershipAccessApi.getAccountDevice(merchant.id).device.token
        } catch (ex: Exception) {
            null
        }
}
