package com.wutsi.blog.mail.service.sender.transaction

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.sender.AbstractBlogMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.mapper.ProductMapper
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.domain.WalletEntity
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import java.text.DecimalFormat
import java.util.Locale

@Service
class OrderMailSender(
    private val mapper: ProductMapper,

    @Value("\${wutsi.application.mail.order.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractBlogMailSender() {
    fun send(transaction: TransactionEntity): String? {
        val merchant = transaction.wallet.user
        val message = createEmailMessage(transaction, merchant)
        return smtp.send(message)
    }

    override fun getUnsubscribeUrl(blog: UserEntity, recipient: UserEntity) = null

    private fun createEmailMessage(transaction: TransactionEntity, merchant: UserEntity) = Message(
        sender = Party(
            displayName = transaction.wallet.user.fullName,
            email = transaction.wallet.user.email ?: "",
        ),
        recipient = Party(
            displayName = transaction.paymentMethodOwner,
            email = transaction.email ?: "",
        ),
        language = transaction.wallet.user.language,
        mimeType = "text/html;charset=UTF-8",
        data = mapOf(),
        subject = messages.getMessage(
            "order.subject",
            arrayOf(),
            Locale(transaction.user?.language ?: getLanguage(merchant))
        ),
        body = generateBody(transaction, merchant, createMailContext(merchant, transaction.user)),
        headers = mapOf(
            "X-SES-CONFIGURATION-SET" to sesConfigurationSet
        )
    )

    private fun generateBody(transaction: TransactionEntity, merchant: UserEntity, mailContext: MailContext): String {
        val merchant = transaction.wallet.user
        val thymleafContext = Context(Locale(merchant.language ?: "en"))
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

    private fun formatMoney(amount: Long, wallet: WalletEntity): String {
        val country = Country.all.find { it.code.equals(wallet.country, true) }
        val fmt = country?.let { DecimalFormat(country.monetaryFormat) }

        return fmt?.let { fmt.format(amount) } ?: "$amount ${country?.currencySymbol}"
    }
}
