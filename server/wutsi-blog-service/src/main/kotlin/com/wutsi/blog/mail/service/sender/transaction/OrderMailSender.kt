package com.wutsi.blog.mail.service.sender.transaction

import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.sender.AbstractBlogMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.mapper.ProductMapper
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import java.util.Locale

@Service
class OrderMailSender(
    private val mapper: ProductMapper,

    @Value("\${wutsi.application.mail.order.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractBlogMailSender() {
    fun send(transaction: TransactionEntity): String? {
        val merchant = transaction.wallet.user
        val language = transaction.user?.language ?: getLanguage(merchant)
        val message = createEmailMessage(transaction, merchant, language)
        return smtp.send(message)
    }

    override fun getUnsubscribeUrl(blog: UserEntity, recipient: UserEntity): String? = null

    private fun createEmailMessage(transaction: TransactionEntity, merchant: UserEntity, language: String) = Message(
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
            "order.subject",
            arrayOf(),
            Locale(language)
        ),
        body = generateBody(transaction, createMailContext(merchant, transaction.user), language),
        headers = mapOf(
            "X-SES-CONFIGURATION-SET" to sesConfigurationSet
        )
    )

    private fun generateBody(transaction: TransactionEntity, mailContext: MailContext, language: String): String {
        val thymleafContext = Context(Locale(language))
        thymleafContext.setVariable("recipientName", transaction.paymentMethodOwner)
        thymleafContext.setVariable("productTitle", transaction.product?.title?.uppercase())
        thymleafContext.setVariable("productPrice", formatMoney(transaction.product!!.price, transaction.wallet))
        thymleafContext.setVariable("productUrl", toProductUrl(transaction.product))
        thymleafContext.setVariable("downloadUrl", toDownloadUrl(transaction))
        thymleafContext.setVariable("context", mailContext)

        val body = templateEngine.process("mail/order.html", thymleafContext)
        return mailFilterSet.filter(body = body, context = mailContext)
    }

    private fun toDownloadUrl(transaction: TransactionEntity): String =
        "$webappUrl/product/${transaction.product?.id}/download/${transaction.id}"

    private fun toProductUrl(product: ProductEntity): String =
        webappUrl + mapper.toSlug(product)
}
