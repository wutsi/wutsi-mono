package com.wutsi.blog.mail.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_DAILY_EMAIL_SENT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.dto.StoryDailyEmailSentPayload
import com.wutsi.blog.story.domain.StoryContentEntity
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

@Service
class DailyMailSender(
    private val smtp: SMTPSender,
    private val templateEngine: TemplateEngine,
    private val mailFilterSet: MailFilterSet,
    private val editorJS: EditorJSService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,

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
    ): Boolean {
        val storyId = content.story.id!!
        if (alreadySent(storyId, recipient)) { // Make sure email never sent more than once!!!
            return false
        }

        val messageId = send(content, blog, recipient)
        if (messageId != null) {
            try {
                notify(
                    storyId = storyId,
                    recipient = recipient,
                    payload = StoryDailyEmailSentPayload(
                        messageId = messageId,
                        email = recipient.email,
                    ),
                )
                return true
            } catch (ex: Exception) {
                LOGGER.warn(
                    "Unable to store event",
                    ex,
                ) // Ignore this error!!! We don't want it this error to get this email to be re-sent!
            }
        }
        return false
    }

    private fun alreadySent(storyId: Long, recipient: UserEntity): Boolean =
        if (eventStore.events(
                streamId = StreamId.STORY,
                type = STORY_DAILY_EMAIL_SENT_EVENT,
                entityId = storyId.toString(),
                userId = recipient.id?.toString(),
            ).isNotEmpty()
        ) {
            LOGGER.warn("Daily email already sent to User#${recipient.id}")
            true
        } else {
            false
        }

    private fun send(content: StoryContentEntity, blog: UserEntity, recipient: UserEntity): String? {
        if (recipient.email.isNullOrEmpty()) {
            return null
        }

        val message = createEmailMessage(content, blog, recipient)
        return smtp.send(message)
    }

    private fun createEmailMessage(content: StoryContentEntity, blog: UserEntity, recipient: UserEntity) = Message(
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
        body = generateBody(content, blog, recipient),
    )

    private fun generateBody(content: StoryContentEntity, blog: UserEntity, recipient: UserEntity): String {
        val mailContext = createMailContext(blog)
        val doc = editorJS.fromJson(content.content)

        val thymleafContext = Context(Locale(blog.language ?: "en"))
        thymleafContext.setVariable("title", content.story.title)
        thymleafContext.setVariable("tagline", content.story.tagline?.ifEmpty { null })
        thymleafContext.setVariable("content", editorJS.toHtml(doc))
        thymleafContext.setVariable(
            "pixelUrl",
            "${mailContext.websiteUrl}/pixel/s${content.story.id}-u${recipient.id}.png",
        )

        val body = templateEngine.process("mail/story.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    private fun createMailContext(blog: UserEntity) = MailContext(
        assetUrl = assetUrl,
        websiteUrl = webappUrl,
        template = "default",
        blog = Blog(
            name = blog.name,
            logoUrl = blog.pictureUrl,
            fullName = blog.fullName,
            facebookUrl = blog.facebookId?.let { "https://www.facebook.com/$it" },
            linkedInUrl = blog.linkedinId?.let { "https://www.linkedin.com/in/$it" },
            twitterUrl = blog.twitterId?.let { "https://www.twitter.com/$it" },
            youtubeUrl = blog.youtubeId?.let { "https://www.youtube.com/$it" },
        ),
    )

    private fun notify(storyId: Long, recipient: UserEntity, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.STORY,
                entityId = storyId.toString(),
                userId = recipient.id?.toString(),
                type = STORY_DAILY_EMAIL_SENT_EVENT,
                timestamp = Date(),
                payload = payload,
            ),
        )
        eventStream.publish(STORY_DAILY_EMAIL_SENT_EVENT, EventPayload(eventId))
    }
}
