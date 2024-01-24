package com.wutsi.blog.mail.service.sender.transaction

import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.mapper.LinkMapper
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.sender.AbstractBlogMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.service.OfferService
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.context.Context
import java.util.Date
import java.util.Locale

@Service
class OrderAbandonedMailSender(
    private val linkMapper: LinkMapper,
    private val offerService: OfferService,
    private val eventStore: EventStore,
    @Value("\${wutsi.application.mail.order-abandoned.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractBlogMailSender() {
    override fun getUnsubscribeUrl(blog: UserEntity, recipient: UserEntity) = null

    @Transactional
    fun send(transaction: TransactionEntity, eventType: String): String? {
        val merchant = transaction.wallet.user
        val language = transaction.user?.language ?: getLanguage(merchant)
        var messageId: String? = null

        if (transaction.product != null) {
            val offers = offerService.search(
                SearchOfferRequest(
                    userId = transaction.user?.id,
                    productIds = listOf(transaction.product.id ?: -1)
                )
            )
            if (offers.isNotEmpty() && !alreadySent(transaction.id!!, eventType)) {
                val message = createProductEmailMessage(transaction, transaction.product, offers[0], merchant, language)
                messageId = smtp.send(message)
            }
        }

        if (messageId != null) {
            notify(transaction.id!!, eventType, transaction.user)
        }
        return messageId
    }

    private fun createProductEmailMessage(
        transaction: TransactionEntity,
        product: ProductEntity,
        offer: Offer,
        merchant: UserEntity,
        language: String,
    ) = Message(
        sender = Party(
            displayName = merchant.fullName,
            email = merchant.email ?: "",
        ),
        recipient = Party(
            displayName = transaction.paymentMethodOwner,
            email = transaction.email ?: "",
        ),
        language = language,
        mimeType = "text/html;charset=UTF-8",
        data = mapOf(),
        subject = messages.getMessage(
            "order_abandoned_daily.subject",
            arrayOf(),
            Locale(language)
        ),
        body = generateProductBody(
            transaction,
            product,
            offer,
            createMailContext(merchant, transaction.user),
            language
        ),
        headers = mapOf(
            "X-SES-CONFIGURATION-SET" to sesConfigurationSet,
        )
    )

    private fun generateProductBody(
        transaction: TransactionEntity,
        product: ProductEntity,
        offer: Offer,
        mailContext: MailContext,
        language: String,
    ): String {
        val thymleafContext = Context(Locale(language))
        thymleafContext.setVariable("recipientName", transaction.paymentMethodOwner)
        thymleafContext.setVariable(
            "link",
            linkMapper.toLinkModel(product, offer, mailContext).copy(url = toBuyUrl(transaction, product))
        )
        thymleafContext.setVariable("context", mailContext)

        val body = templateEngine.process("mail/order-abandoned-daily.html", thymleafContext)
        return mailFilterSet.filter(body = body, context = mailContext)
    }

    private fun toBuyUrl(transaction: TransactionEntity, product: ProductEntity): String =
        webappUrl + "/buy?product-id=${product.id}&from-tx=${transaction.id}"

    private fun alreadySent(transactionId: String, type: String): Boolean =
        eventStore.events(
            streamId = StreamId.TRANSACTION,
            entityId = transactionId,
            type = type,
        ).isNotEmpty()

    private fun notify(transactionId: String, type: String, recipient: UserEntity?) {
        eventStore.store(
            Event(
                streamId = StreamId.TRANSACTION,
                entityId = transactionId,
                userId = recipient?.id?.toString(),
                type = type,
                timestamp = Date(),
            ),
        )
    }
}
