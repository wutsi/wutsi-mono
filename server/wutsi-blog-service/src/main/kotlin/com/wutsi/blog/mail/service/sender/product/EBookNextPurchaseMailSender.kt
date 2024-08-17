package com.wutsi.blog.mail.service.sender.product

import com.wutsi.blog.event.EventType.PRODUCT_NEXT_PURCHASE_EMAIL_SENT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.mapper.LinkMapper
import com.wutsi.blog.mail.service.sender.AbstractBlogMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.product.dto.SearchProductContext
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
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
class EBookNextPurchaseMailSender(
    private val linkMapper: LinkMapper,
    private val eventStore: EventStore,
    private val productService: ProductService,
    private val userService: UserService,

    @Value("\${wutsi.application.mail.ebook-next-purchase.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractBlogMailSender() {
    @Transactional
    fun send(
        product: ProductEntity,
        recipient: UserEntity,
    ): Boolean {
        val storeId = product.store.id ?: "-"

        // Products
        val products = findOtherProducts(storeId, product, recipient)
        if (products.isEmpty()) {
            return false
        }

        // Author
        val author = userService.search(
            SearchUserRequest(
                storeIds = listOf(storeId),
                limit = 1
            )
        ).firstOrNull()
        if (author == null) {
            return false
        }

        val message = createEmailMessage(author, recipient, products)
        val messageId = smtp.send(message)
        if (messageId != null) {
            notify(product, recipient)
        }

        return messageId != null
    }

    private fun findOtherProducts(storeId: String, product: ProductEntity, recipient: UserEntity): List<ProductEntity> =
        productService.searchProducts(
            SearchProductRequest(
                types = listOf(ProductType.EBOOK, ProductType.COMICS),
                excludeProductIds = product.id?.let { id -> listOf(id) } ?: emptyList(),
                storeIds = listOf(storeId),
                status = ProductStatus.PUBLISHED,
                available = true,
                excludePurchasedProduct = true,
                sortBy = ProductSortStrategy.RECOMMENDED,
                searchContext = SearchProductContext(
                    userId = recipient.id,
                ),
                limit = 10,
            )
        )

    private fun createEmailMessage(
        author: UserEntity,
        recipient: UserEntity,
        products: List<ProductEntity>,
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
            subject = messages.getMessage("ebook-next-purchase.subject", arrayOf(products.size), Locale(language)),
            body = generateBody(author, recipient, products, language),
            headers = mapOf(
                "X-SES-CONFIGURATION-SET" to sesConfigurationSet,
            )
        )
    }

    private fun generateBody(
        author: UserEntity,
        recipient: UserEntity,
        products: List<ProductEntity>,
        language: String,
    ): String {
        val mailContext = createMailContext(author, recipient)

        val thymleafContext = Context(Locale(language))
        thymleafContext.setVariable("recipientName", recipient.fullName)

        thymleafContext.setVariable(
            "books",
            products.map { product -> linkMapper.toLinkModel(product, null, mailContext, "email-next-purchase") }
        )

        val body = templateEngine.process("mail/ebook-next-purchase.html", thymleafContext)
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
                type = PRODUCT_NEXT_PURCHASE_EMAIL_SENT_EVENT,
                timestamp = Date(),
            ),
        )
    }
}
