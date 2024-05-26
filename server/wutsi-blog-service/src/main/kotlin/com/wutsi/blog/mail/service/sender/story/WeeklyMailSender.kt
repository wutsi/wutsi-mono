package com.wutsi.blog.mail.service.sender.story

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.AdsType
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsService
import com.wutsi.blog.mail.mapper.AdsMapper
import com.wutsi.blog.mail.mapper.LinkMapper
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.model.AdsModel
import com.wutsi.blog.mail.service.model.LinkModel
import com.wutsi.blog.mail.service.sender.AbstractWutsiMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.Offer
import com.wutsi.blog.product.dto.SearchOfferRequest
import com.wutsi.blog.product.service.OfferService
import com.wutsi.blog.product.service.ProductSearchFilterSet
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryContext
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.service.StorySearchFilterSet
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
    private val linkMapper: LinkMapper,
    private val adsMapper: AdsMapper,
    private val offerService: OfferService,
    private val adsService: AdsService,
    private val storySearchFilterSet: StorySearchFilterSet,
    private val productSearchFilterSet: ProductSearchFilterSet,

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

        // Personalize the list of stories
        val xstories = personalize(stories, recipient)
        if (xstories.isEmpty()) {
            return false
        }

        // Personalize the list of products

        // Ads
        val ads = loadAds(recipient)

        val message = createEmailMessage(xstories, users, recipient, products, ads)
        return smtp.send(message) != null
    }

    private fun personalize(stories: List<StoryEntity>, recipient: UserEntity): List<StoryEntity> {
        val xstories = storySearchFilterSet.filter(
            SearchStoryRequest(
                sortBy = StorySortStrategy.RECOMMENDED,
                bubbleDownViewedStories = true,
                excludeStoriesFromSubscriptions = true,
                searchContext = SearchStoryContext(
                    userId = recipient.id
                )
            ),
            stories
        )

        return xstories.filter { story ->
            story.language == recipient.language && // Same language
                    story.userId != recipient.id // Not published by the recipient
        }.take(10)
    }

    private fun createEmailMessage(
        stories: List<StoryEntity>,
        users: List<UserEntity>,
        recipient: UserEntity,
        products: List<ProductEntity>,
        ads: List<AdsEntity>,
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
            ads,
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
        ads: List<AdsEntity>,
        mailContext: MailContext,
    ): String {
        val thymleafContext = Context(Locale(recipient.language ?: "en"))
        thymleafContext.setVariable("recipientName", recipient.fullName)
        thymleafContext.setVariable("stories", toStoryLinkModel(stories, users, mailContext))
        thymleafContext.setVariable("context", mailContext)

        val banner = getAds(ads, listOf(AdsType.BANNER_MOBILE))
        if (banner != null) {
            thymleafContext.setVariable("adsBanner", banner)
            thymleafContext.setVariable("adsBannerPixelUrl", adsMapper.getAdsPixelUrl(banner, recipient))
        }

        val box1 = getAds(ads, listOf(AdsType.BOX_2X, AdsType.BOX))
        if (box1 != null) {
            thymleafContext.setVariable("adsBox1", box1)
            thymleafContext.setVariable("adsBox1PixelUrl", adsMapper.getAdsPixelUrl(box1, recipient))
        }

        val box2 = getAds(ads, listOf(AdsType.BOX_2X, AdsType.BOX))
        if (box2 != null) {
            thymleafContext.setVariable("adsBox2", box2)
            thymleafContext.setVariable("adsBox2PixelUrl", adsMapper.getAdsPixelUrl(box2, recipient))
        }

        if (products.isNotEmpty()) {
            val offers = offerService.search(
                SearchOfferRequest(
                    userId = recipient.id,
                    productIds = products.mapNotNull { it.id },
                )
            )
            thymleafContext.setVariable(
                "productChunks",
                toProductLinkModel(products, offers, mailContext)
                    .take(18)
                    .chunked(3)
            )
        }

        val body = templateEngine.process("mail/weekly-digest.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    private fun getAds(ads: List<AdsEntity>, types: List<AdsType>): AdsModel? =
        ads.shuffled()
            .find { types.contains(it.type) }
            ?.let { adsMapper.toAdsModel(it) }

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

    private fun loadAds(recipient: UserEntity): List<AdsEntity> =
        adsService.searchAds(
            SearchAdsRequest(
                status = listOf(AdsStatus.RUNNING),
                type = listOf(AdsType.BOX, AdsType.BOX_2X, AdsType.BANNER_MOBILE),
                limit = 20,
                impressionContext = AdsImpressionContext(
                    userId = recipient.id,
                    adsPerType = 3,
                    email = true,
                    userAgent = ADS_USER_AGENT
                )
            )
        )
}
