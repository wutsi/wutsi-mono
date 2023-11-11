package com.wutsi.blog.mail.service

import com.wutsi.blog.backend.PersonalizeBackend
import com.wutsi.blog.mail.service.model.LinkModel
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.ml.personalize.dto.SortStoryRequest
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale
import javax.annotation.PostConstruct

@Service
class WeeklyMailSender(
    private val smtp: SMTPSender,
    private val personalizeBackend: PersonalizeBackend,
    private val subscriptionService: SubscriptionService,
    private val templateEngine: TemplateEngine,
    private val mailFilterSet: MailFilterSet,
    private val mapper: StoryMapper,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
    @Value("\${wutsi.application.website-url}") private val webappUrl: String,
    @Value("\${wutsi.application.mail.weekly-digest.whitelist}") private val whitelist: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WeeklyMailSender::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info(">>> Email Whitelist: $whitelist")
    }

    @Transactional
    fun send(stories: List<StoryEntity>, users: List<UserEntity>, recipient: UserEntity): Boolean {
        if (recipient.email.isNullOrEmpty() || !isWhitelisted(recipient.email!!)) {
            return false
        }

        val xstories = filterOutStoriesFromSubscriptions(
            stories = sort(stories, recipient),
            recipient = recipient
        )
            .filter { it.userId != recipient.id }
            .take(10)
        if (xstories.isEmpty()) {
            return false
        }

        val message = createEmailMessage(xstories, users, recipient)
        return smtp.send(message) != null
    }

    private fun filterOutStoriesFromSubscriptions(
        stories: List<StoryEntity>,
        recipient: UserEntity
    ): List<StoryEntity> {
        val userIds = subscriptionService.search(
            SearchSubscriptionRequest(
                subscriberId = recipient.id,
                limit = 100
            )
        ).map { it.userId }
        return stories.filter { !userIds.contains(it.id) }
    }

    private fun sort(stories: List<StoryEntity>, recipient: UserEntity): List<StoryEntity> {
        try {
            val xstories = personalizeBackend.sort(
                SortStoryRequest(
                    storyIds = stories.mapNotNull { it.id },
                    userId = recipient.id!!,
                )
            ).stories

            val map = stories.associateBy { it.id }
            return xstories.mapNotNull { map[it.id] }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to sort stories for User#${recipient.id}", ex)
            return stories
        }
    }

    private fun createEmailMessage(
        stories: List<StoryEntity>,
        users: List<UserEntity>,
        recipient: UserEntity,
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
        body = generateBody(stories, users, recipient, createMailContext(recipient)),
    )

    private fun generateBody(
        stories: List<StoryEntity>,
        users: List<UserEntity>,
        recipient: UserEntity,
        mailContext: MailContext,
    ): String {
        val thymleafContext = Context(Locale(recipient.language ?: "en"))
        thymleafContext.setVariable("recipientName", recipient.fullName)
        thymleafContext.setVariable("stories", toLinkModel(stories, users, mailContext))
        thymleafContext.setVariable("context", mailContext)

        val body = templateEngine.process("mail/weekly-digest.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    private fun toLinkModel(
        stories: List<StoryEntity>,
        users: List<UserEntity>,
        mailContext: MailContext
    ): List<LinkModel> {
        val userMap = users.associateBy { it.id }
        return stories.map { story ->
            LinkModel(
                title = story.title ?: "",
                url = mailContext.websiteUrl + mapper.slug(story) + "?referer=weekly-digest",
                summary = story.summary,
                thumbnailUrl = story.thumbnailUrl,
                author = userMap[story.userId]?.fullName,
                authorPictureUrl = userMap[story.userId]?.pictureUrl,
            )
        }
    }

    private fun createMailContext(recipient: UserEntity): MailContext {
        return MailContext(
            assetUrl = assetUrl,
            websiteUrl = webappUrl,
            template = "default",
            storyId = null,
            blog = Blog(
                name = null,
                fullName = "Wutsi",
                language = recipient.language ?: "en",
                logoUrl = "$assetUrl/assets/wutsi/img/logo/logo_512x512.png",
            ),
        )
    }

    private fun isWhitelisted(email: String): Boolean =
        whitelist == "*" || whitelist.contains(email)
}
