package com.wutsi.blog.mail.service.sender.story

import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.event.EventType.STORY_DAILY_EMAIL_SENT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.dto.StoryDailyEmailSentPayload
import com.wutsi.blog.mail.mapper.LinkMapper
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.model.LinkModel
import com.wutsi.blog.mail.service.sender.AbstractBlogMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.DiscountType
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.service.DiscountService
import com.wutsi.blog.product.service.OfferService
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.story.service.EditorJSService
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
import java.util.UUID

@Service
class DailyMailSender(
    private val editorJS: EditorJSService,
    private val eventStore: EventStore,
    private val storyMapper: StoryMapper,
    private val linkMapper: LinkMapper,
    private val offerService: OfferService,
    private val discountService: DiscountService,

    @Value("\${wutsi.application.mail.daily-newsletter.ses-configuration-set}") private val sesConfigurationSet: String,
) : AbstractBlogMailSender() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DailyMailSender::class.java)
        const val HEADER_STORY_ID = "X-Wutsi-Story-Id"
        const val HEADER_UNSUBSCRIBE = "List-Unsubscribe"
    }

    @Transactional
    fun send(
        blog: UserEntity,
        store: StoreEntity?,
        content: StoryContentEntity,
        recipient: UserEntity,
        otherStories: List<StoryEntity>,
        products: List<ProductEntity>,
    ): Boolean {
        val storyId = content.story.id!!

        if (recipient.email.isNullOrEmpty()) {
            return false
        }
        if (alreadySent(storyId, recipient)) { // Make sure email never sent more than once!!!
            LOGGER.warn("story_id=$storyId email=${recipient.email} - Already send")
            return false
        }

        val message = createEmailMessage(content, blog, store, recipient, otherStories, products)
        val messageId = smtp.send(message)

        if (messageId != null) {
            try {
                notify(
                    storyId = storyId,
                    type = STORY_DAILY_EMAIL_SENT_EVENT,
                    recipient = recipient,
                    payload = StoryDailyEmailSentPayload(
                        messageId = messageId,
                        email = recipient.email,
                    ),
                )
                return true
            } catch (ex: Exception) {
                LOGGER.warn("story_id=$storyId email=${recipient.email} - Already send", ex)
            }
        }
        return false
    }

    override fun getUnsubscribeUrl(blog: UserEntity, recipient: UserEntity): String =
        "$webappUrl/@/${blog.name}/unsubscribe?email=${recipient.email}"

    private fun alreadySent(storyId: Long, recipient: UserEntity): Boolean =
        eventStore.events(
            streamId = StreamId.STORY,
            type = STORY_DAILY_EMAIL_SENT_EVENT,
            entityId = storyId.toString(),
            userId = recipient.id?.toString(),
        ).isNotEmpty()

    private fun createEmailMessage(
        content: StoryContentEntity,
        blog: UserEntity,
        store: StoreEntity?,
        recipient: UserEntity,
        otherStories: List<StoryEntity>,
        products: List<ProductEntity>,
    ) = Message(
        sender = Party(
            displayName = blog.fullName,
            email = blog.email ?: "",
        ),
        recipient = Party(
            email = recipient.email ?: "",
            displayName = recipient.fullName,
        ),
        language = recipient.language,
        mimeType = "text/html;charset=UTF-8",
        data = mapOf(),
        subject = content.story.title,
        body = generateBody(content, blog, store, recipient, otherStories, products),
        headers = mapOf(
            HEADER_STORY_ID to content.story.id.toString(),
            HEADER_UNSUBSCRIBE to "<" + getUnsubscribeUrl(blog, recipient) + ">",
            "X-SES-CONFIGURATION-SET" to sesConfigurationSet,
        )
    )

    private fun generateBody(
        content: StoryContentEntity,
        blog: UserEntity,
        store: StoreEntity?,
        recipient: UserEntity,
        otherStories: List<StoryEntity>,
        products: List<ProductEntity>,
    ): String {
        val story = content.story
        val storyId = content.story.id
        val summary = content.story.access == StoryAccess.DONOR
        val mailContext = createMailContext(blog, recipient, content.story)
        val doc = editorJS.fromJson(content.content, summary)
        val slug = storyMapper.slug(story, story.language)

        val thymleafContext = Context(Locale(blog.language ?: "en"))
        thymleafContext.setVariable("recipientName", recipient.fullName)
        thymleafContext.setVariable("title", content.story.title)
        thymleafContext.setVariable("summary", summary)
        thymleafContext.setVariable("tagline", content.story.tagline?.ifEmpty { null })
        thymleafContext.setVariable("content", editorJS.toHtml(doc))
        thymleafContext.setVariable("assetUrl", mailContext.assetUrl)
        thymleafContext.setVariable("storyUrl", mailContext.websiteUrl + storyMapper.slug(story))
        thymleafContext.setVariable("commentUrl", mailContext.websiteUrl + "/comments?story-id=$storyId")
        thymleafContext.setVariable("shareUrl", mailContext.websiteUrl + "$slug?share=1")
        thymleafContext.setVariable(
            "likeUrl",
            mailContext.websiteUrl + "$slug?like=1&like-key=${UUID.randomUUID()}_${storyId}_${recipient.id}",
        )
        if (!summary) {
            thymleafContext.setVariable(
                "pixelUrl",
                "${mailContext.websiteUrl}/pixel/s${content.story.id}-u${recipient.id}.png?ss=${content.story.id}&uu=${recipient.id}&rr=" + UUID.randomUUID(),
            )
        }
        if (otherStories.isNotEmpty()) {
            thymleafContext.setVariable("otherStoryLinks", toLinkModel(otherStories, mailContext))
        }
        thymleafContext.setVariable("context", mailContext)
        thymleafContext.setVariable("adsBanner", toAdsBannerLinkModel())

        if (products.isNotEmpty()) {
            val offers = offerService.search(
                SearchOfferRequest(
                    userId = recipient.id,
                    productIds = products.mapNotNull { it.id },
                )
            )

            val offerWithCoupon = offers.find { it.discount?.type == DiscountType.COUPON }
            val productWidthCoupon = products.find { product -> product.id == offerWithCoupon?.productId }
            if (productWidthCoupon != null) {
                thymleafContext.setVariable("couponPercentage", offerWithCoupon?.discount?.percentage)
                thymleafContext.setVariable(
                    "productWithCoupon",
                    toLinkModel(
                        listOf(productWidthCoupon),
                        offers,
                        mailContext
                    ).first()
                )
            }

            thymleafContext.setVariable("shopUrl", "$webappUrl/@/${blog.name}/shop")
            thymleafContext.setVariable(
                "productChunks",
                toLinkModel(products, offers, mailContext)
                    .take(18)
                    .chunked(3)
            )

            if (store?.enableDonationDiscount == true) {
                thymleafContext.setVariable("donationDiscount", true)
                thymleafContext.setVariable("donationUrl", "$webappUrl/@/${blog.name}/donate")

                val country = blog.country?.let { country -> Country.fromCode(country) }
                if (country != null) {
                    thymleafContext.setVariable(
                        "donationAmount",
                        country.createMoneyFormat().format(country.defaultDonationAmounts[0])
                    )
                }
            }
        }

        val body = templateEngine.process("mail/story.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    private fun toLinkModel(stories: List<StoryEntity>, mailContext: MailContext): List<LinkModel> =
        stories.map { story -> linkMapper.toLinkModel(story, mailContext) }

    private fun toLinkModel(
        products: List<ProductEntity>,
        offers: List<Offer>,
        mailContext: MailContext,
    ): List<LinkModel> {
        val offerMap = offers.associateBy { offer -> offer.productId }
        return products
            .map { product -> linkMapper.toLinkModel(product, offerMap[product.id], mailContext) }
    }

    protected fun toAdsBannerLinkModel(): LinkModel? = null
//    LinkModel(
//        imageUrl = "$assetUrl/assets/wutsi/img/ads/best-talent-cm/banner-mobile.png",
//        title = "Best Talent Cameroon",
//        url = "https://btc4.dotchoize.com",
//    )

    private fun notify(storyId: Long, type: String, recipient: UserEntity, payload: Any? = null) {
        eventStore.store(
            Event(
                streamId = StreamId.STORY,
                entityId = storyId.toString(),
                userId = recipient.id?.toString(),
                type = type,
                timestamp = Date(),
                payload = payload,
            ),
        )
    }
}
