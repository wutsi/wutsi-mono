package com.wutsi.blog.mail.service.sender.product

import com.wutsi.blog.event.EventType.PRODUCT_EBOOK_LAUNCH_EMAIL_SENT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.mapper.LinkMapper
import com.wutsi.blog.mail.service.sender.AbstractBlogMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.context.Context
import java.util.Date
import java.util.Locale

@Service
class EBookLaunchMailSender(
    private val linkMapper: LinkMapper,
    private val eventStore: EventStore,

    @Value("\${wutsi.application.mail.e-book-launch.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractBlogMailSender() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EBookLaunchMailSender::class.java)
    }

    @Transactional
    fun send(
        product: ProductEntity,
        author: UserEntity,
        recipient: UserEntity,
    ): Boolean {
        if (product.type != ProductType.EBOOK) {
            return false
        }

        val message = createEmailMessage(product, author, recipient)
        val messageId = smtp.send(message)
        if (messageId != null) {
            try {
                notify(
                    product = product,
                    recipient = recipient,
                )
                return true
            } catch (ex: Exception) {
                LOGGER.warn("product_id=${product.id} email=${recipient.email} - Already send", ex)
            }
        }

        return messageId != null
    }

    private fun createEmailMessage(
        product: ProductEntity,
        author: UserEntity,
        recipient: UserEntity,
    ): Message {
        val language = getLanguage(recipient)
        return Message(
            sender = Party(
                displayName = author.fullName,
                email = author.email ?: "",
            ),
            recipient = Party(
                email = recipient.email ?: "",
                displayName = recipient.fullName,
            ),
            language = language,
            mimeType = "text/html;charset=UTF-8",
            data = mapOf(),
            subject = messages.getMessage(
                "ebook-launch.subject",
                arrayOf(product.title.uppercase()),
                Locale(language)
            ),
            body = generateBody(product, author, recipient, language),
            headers = mapOf(
                "X-SES-CONFIGURATION-SET" to sesConfigurationSet,
            )
        )
    }

    private fun generateBody(
        product: ProductEntity,
        author: UserEntity,
        recipient: UserEntity,
        language: String,
    ): String {
        val mailContext = createMailContext(author, recipient)

        val thymleafContext = Context(Locale(language))
        thymleafContext.setVariable("recipientName", recipient.fullName)
        thymleafContext.setVariable("book", linkMapper.toLinkModel(product, null, mailContext))

        val body = templateEngine.process("mail/ebook-launch.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    private fun notify(product: ProductEntity, recipient: UserEntity) {
        eventStore.store(
            Event(
                streamId = StreamId.PRODUCT,
                entityId = product.id!!.toString(),
                userId = recipient.id?.toString(),
                type = PRODUCT_EBOOK_LAUNCH_EMAIL_SENT_EVENT,
                timestamp = Date(),
            ),
        )
    }
}
