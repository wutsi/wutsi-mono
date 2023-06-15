package com.wutsi.blog.story.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_EMAIL_NOTIFICATION_SENT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.service.Blog
import com.wutsi.blog.mail.service.MailContext
import com.wutsi.blog.mail.service.MailFilterSet
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.blog.story.dto.SendStoryEmailNotificationCommand
import com.wutsi.blog.story.dto.StoryEmailNotificationSentPayload
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
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
import kotlin.jvm.optionals.getOrNull

@Service
class StoryEmailNotificationSender(
    private val messagingServiceProvider: MessagingServiceProvider,
    private val logger: KVLogger,
    private val templateEngine: TemplateEngine,
    private val mailFilterSet: MailFilterSet,
    private val editorJS: EditorJSService,
    private val storyService: StoryService,
    private val userService: UserService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
    @Value("\${wutsi.application.website-url}") private val webappUrl: String,
    @Value("\${wutsi.application.notification.debug}") private val debugNotification: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryEmailNotificationSender::class.java)
    }

    @Transactional
    fun send(command: SendStoryEmailNotificationCommand) {
        logger.add("recipient_id", command.recipientId)
        logger.add("story_id", command.storyId)

        val payload = execute(command) ?: return
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.STORY,
                entityId = command.storyId.toString(),
                userId = command.recipientId.toString(),
                type = STORY_EMAIL_NOTIFICATION_SENT_EVENT,
                timestamp = Date(),
                payload = payload,
            ),
        )
        try {
            eventStream.publish(STORY_EMAIL_NOTIFICATION_SENT_EVENT, EventPayload(eventId))
        } catch (ex: Exception) {
            // NOTHING... ignore
        }
    }

    private fun execute(command: SendStoryEmailNotificationCommand): StoryEmailNotificationSentPayload? {
        if (alreadySent(command)) { // Make sure email never sent more than once!!!
            return null
        }

        val story = storyService.findById(command.storyId)
        val content = storyService.findContent(story, story.language).getOrNull() ?: return null

        val blog = userService.findById(story.userId)
        val recipient = userService.findById(command.recipientId)
        val messageId = send(content, blog, recipient) ?: return null

        return StoryEmailNotificationSentPayload(
            messageId = messageId,
            email = recipient.email,
        )
    }

    private fun alreadySent(command: SendStoryEmailNotificationCommand): Boolean =
        eventStore.events(
            streamId = StreamId.STORY,
            type = STORY_EMAIL_NOTIFICATION_SENT_EVENT,
            entityId = command.storyId.toString(),
            userId = command.recipientId.toString(),
        ).isNotEmpty()

    private fun send(content: StoryContentEntity, blog: UserEntity, recipient: UserEntity): String? {
        if (recipient.email.isNullOrEmpty()) {
            logger.add("delivery_error", "no-recipient-email")
            return null
        }

        val message = createEmailMessage(content, blog, recipient)
        debug(message)
        val messageId = messagingServiceProvider.get(MessagingType.EMAIL).send(message)
        logger.add("message_id", messageId)
        logger.add("email_sent", true)
        return messageId
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

        val body = templateEngine.process("/mail/story.html", thymleafContext)
        return mailFilterSet.filter(
            body = body,
            context = mailContext,
        )
    }

    protected fun debug(message: Message) {
        if (debugNotification.toBoolean()) {
            LOGGER.info("To: ${message.recipient.displayName}< ${message.recipient.email}>")
            LOGGER.info("Subject: ${message.subject}>")
            LOGGER.info("${message.body}\n")
        }
    }

    protected fun createMailContext(blog: UserEntity) = MailContext(
        assetUrl = assetUrl,
        websiteUrl = webappUrl,
        template = "default",
        blog = Blog(
            name = blog.name,
            logoUrl = blog.pictureUrl,
            fullName = blog.fullName,
        ),
    )
}
