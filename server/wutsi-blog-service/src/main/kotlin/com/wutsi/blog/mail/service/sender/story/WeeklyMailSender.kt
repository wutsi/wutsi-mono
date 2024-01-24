package com.wutsi.blog.mail.service.sender.story

import com.wutsi.blog.mail.mapper.LinkMapper
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.model.LinkModel
import com.wutsi.blog.mail.service.sender.AbstractWutsiMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.service.OfferService
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import java.util.Locale
import javax.annotation.PostConstruct

@Service
class WeeklyMailSender(
    private val subscriptionService: SubscriptionService,
    private val linkMapper: LinkMapper,
    private val offerService: OfferService,

    @Value("\${wutsi.application.mail.weekly-digest.whitelist.email}") private val emailWhitelist: String,
    @Value("\${wutsi.application.mail.weekly-digest.whitelist.country}") private val countryWhitelist: String,
    @Value("\${wutsi.application.mail.weekly-digest.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractWutsiMailSender() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WeeklyMailSender::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info(">>> Email Whitelist: $emailWhitelist")
        LOGGER.info(">>> Country Whitelist: $countryWhitelist")
    }

    fun send(
        stories: List<StoryEntity>,
        users: List<UserEntity>,
        recipient: UserEntity,
        products: List<ProductEntity>,
    ): Boolean {
        if (!isWhitelisted(recipient)) {
            return false
        }

        // Remove stories that I'm subscribed to
        val xstories = dedupByUser(
            filterOutStoriesFromSubscriptions(
                stories = stories.filter { it.language == recipient.language && it.userId != recipient.id },
                recipient = recipient
            )
        ).take(10) // Top 10
        if (xstories.isEmpty()) {
            return false
        }

        val message = createEmailMessage(xstories, users, recipient, products)
        return smtp.send(message) != null
    }

    private fun dedupByUser(stories: List<StoryEntity>): List<StoryEntity> {
        val userIds = mutableSetOf<Long>()
        return stories.filter { userIds.add(it.userId) }
    }

    private fun filterOutStoriesFromSubscriptions(
        stories: List<StoryEntity>,
        recipient: UserEntity,
    ): List<StoryEntity> {
        val userIds = subscriptionService.search(
            SearchSubscriptionRequest(
                subscriberId = recipient.id,
                limit = 100
            )
        ).map { it.userId }
        return stories.filter { !userIds.contains(it.id) }
    }

    private fun createEmailMessage(
        stories: List<StoryEntity>,
        users: List<UserEntity>,
        recipient: UserEntity,
        products: List<ProductEntity>,
    ) = Message(
        sender = Party(
            displayName = "Wutsi Weekly Digest",
        ),
        recipient = Party(
            email = recipient.email ?: "",
            displayName = recipient.fullName,
        ),
        language = recipient.language,
        mimeType = "text/html;charset=UTF-8",
        data = mapOf(),
        subject = stories[0].title,
        body = generateBody(
            stories,
            users,
            recipient,
            products,
            createMailContext(recipient.fullName, getLanguage(recipient))
        ),
        headers = mapOf(
            "X-SES-CONFIGURATION-SET" to sesConfigurationSet
        )
    )

    private fun generateBody(
        stories: List<StoryEntity>,
        users: List<UserEntity>,
        recipient: UserEntity,
        products: List<ProductEntity>,
        mailContext: MailContext,
    ): String {
        val thymleafContext = Context(Locale(recipient.language ?: "en"))
        thymleafContext.setVariable("recipientName", recipient.fullName)
        thymleafContext.setVariable("stories", toStoryLinkModel(stories, users, mailContext))
        thymleafContext.setVariable("context", mailContext)

        if (products.isNotEmpty()) {
            val offers = offerService.search(
                SearchOfferRequest(
                    userId = recipient.id,
                    productIds = products.mapNotNull { it.id },
                )
            )
            thymleafContext.setVariable("products", toProductLinkModel(products, offers, mailContext))
        }

        val body = templateEngine.process("mail/weekly-digest.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    private fun toStoryLinkModel(
        stories: List<StoryEntity>,
        users: List<UserEntity>,
        mailContext: MailContext,
    ): List<LinkModel> {
        val userMap = users.associateBy { user -> user.id }
        return stories.map { story -> linkMapper.toLinkModel(story, mailContext, userMap[story.userId]) }
    }

    private fun toProductLinkModel(
        products: List<ProductEntity>,
        offers: List<Offer>,
        mailContext: MailContext,
    ): List<LinkModel> {
        val offerMap = offers.associateBy { offer -> offer.productId }
        return products
            .take(3)
            .map { product -> linkMapper.toLinkModel(product, offerMap[product.id], mailContext) }
    }

    private fun isWhitelisted(recipient: UserEntity): Boolean {
        val email = recipient.email
        val country = recipient.country
        return !email.isNullOrEmpty() &&
                (emailWhitelist == "*" || emailWhitelist.contains(email)) &&
                !country.isNullOrEmpty() &&
                (countryWhitelist == "*" || countryWhitelist.contains(country))
    }
}
