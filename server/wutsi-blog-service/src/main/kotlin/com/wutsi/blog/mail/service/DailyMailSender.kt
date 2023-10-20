package com.wutsi.blog.mail.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_DAILY_EMAIL_SENT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.dto.StoryDailyEmailSentPayload
import com.wutsi.blog.mail.service.model.LinkModel
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.story.service.EditorJSService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.Party
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Date
import java.util.Locale
import java.util.UUID

@Service
class DailyMailSender(
    private val smtp: SMTPSender,
    private val templateEngine: TemplateEngine,
    private val mailFilterSet: MailFilterSet,
    private val editorJS: EditorJSService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val mapper: StoryMapper,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
    @Value("\${wutsi.application.website-url}") private val webappUrl: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DailyMailSender::class.java)
    }

    @Transactional
    fun send(
        blog: UserEntity,
        content: StoryContentEntity,
        recipient: UserEntity,
        otherStories: List<StoryEntity>,
    ): Boolean {
        val storyId = content.story.id!!

        if (recipient.email.isNullOrEmpty()) {
            return false
        }
        if (alreadySent(storyId, recipient)) { // Make sure email never sent more than once!!!
            LOGGER.warn("story_id=$storyId email=${recipient.email} - Already send")
            return false
        }

        val message = createEmailMessage(content, blog, recipient, otherStories)
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
        recipient: UserEntity,
        otherStories: List<StoryEntity>,
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
        body = generateBody(content, blog, recipient, otherStories),
    )

    private fun generateBody(
        content: StoryContentEntity,
        blog: UserEntity,
        recipient: UserEntity,
        otherStories: List<StoryEntity>,
    ): String {
        val story = content.story
        val storyId = content.story.id
        val mailContext = createMailContext(blog)
        val doc = editorJS.fromJson(content.content)
        val slug = mapper.slug(story, story.language)

        val thymleafContext = Context(Locale(blog.language ?: "en"))
        thymleafContext.setVariable("title", content.story.title)
        thymleafContext.setVariable("tagline", content.story.tagline?.ifEmpty { null })
        thymleafContext.setVariable("content", editorJS.toHtml(doc))
        thymleafContext.setVariable("storyUrl", mailContext.websiteUrl + mapper.slug(story))
        thymleafContext.setVariable("commentUrl", mailContext.websiteUrl + "/comments?story-id=$storyId")
        thymleafContext.setVariable("shareUrl", mailContext.websiteUrl + "$slug?share=1")
        thymleafContext.setVariable(
            "likeUrl",
            mailContext.websiteUrl + "$slug?like=1&like-key=${UUID.randomUUID()}_${storyId}_${recipient.id}",
        )
        thymleafContext.setVariable(
            "pixelUrl",
            "${mailContext.websiteUrl}/pixel/s${content.story.id}-u${recipient.id}.png?ss=${content.story.id}&uu=${recipient.id}&rr=" + UUID.randomUUID(),
        )
        thymleafContext.setVariable("assetUrl", mailContext.assetUrl)
        thymleafContext.setVariable("otherStoryLinks", toOtherStoryLinks(otherStories, mailContext))

        val body = templateEngine.process("mail/story.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    private fun toOtherStoryLinks(otherStories: List<StoryEntity>, mailContext: MailContext): List<LinkModel> =
        otherStories.map { story ->
            LinkModel(
                title = story.title ?: "",
                url = mailContext.websiteUrl + mapper.slug(story) + "?utm_from=email-read-also",
                summary = story.summary,
                thumbnailUrl = story.thumbnailUrl,
            )
        }

    private fun createMailContext(blog: UserEntity): MailContext {
        return MailContext(
            assetUrl = assetUrl,
            websiteUrl = webappUrl,
            template = "default",
            blog = Blog(
                name = blog.name,
                logoUrl = blog.pictureUrl,
                fullName = blog.fullName,
                language = blog.language ?: "en",
                facebookUrl = blog.facebookId?.let { "https://www.facebook.com/$it" },
                linkedInUrl = blog.linkedinId?.let { "https://www.linkedin.com/in/$it" },
                twitterUrl = blog.twitterId?.let { "https://www.twitter.com/$it" },
                youtubeUrl = blog.youtubeId?.let { "https://www.youtube.com/$it" },
                githubUrl = blog.githubId?.let { "https://www.github.com/$it" },
                whatsappUrl = blog.whatsappId?.let { "https://wa.me/" + formatPhoneNumber(it) },
                subscribedUrl = null,
                unsubscribedUrl = "$webappUrl/@/${blog.name}/unsubscribe",
            ),
        )
    }

    private fun formatPhoneNumber(number: String): String =
        if (number.startsWith("+")) {
            number.substring(1)
        } else {
            number
        }

    private fun notify(storyId: Long, type: String, recipient: UserEntity, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.STORY,
                entityId = storyId.toString(),
                userId = recipient.id?.toString(),
                type = type,
                timestamp = Date(),
                payload = payload,
            ),
        )
        eventStream.enqueue(type, EventPayload(eventId))
    }
}
