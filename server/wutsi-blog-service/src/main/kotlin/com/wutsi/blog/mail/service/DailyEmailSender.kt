package com.wutsi.blog.mail.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.STORY_DAILY_EMAIL_SENT_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.mail.dto.StoryDailyEmailSentPayload
import com.wutsi.blog.story.domain.StoryContentEntity
import com.wutsi.blog.story.service.EditorJSService
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.service.SubscriptionService
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
import javax.annotation.PostConstruct
import kotlin.jvm.optionals.getOrNull

@Service
class DailyEmailSender(
    private val messagingServiceProvider: MessagingServiceProvider,
    private val logger: KVLogger,
    private val templateEngine: TemplateEngine,
    private val mailFilterSet: MailFilterSet,
    private val editorJS: EditorJSService,
    private val storyService: StoryService,
    private val userService: UserService,
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val subscriptionService: SubscriptionService,

    @Value("\${wutsi.application.asset-url}") private val assetUrl: String,
    @Value("\${wutsi.application.website-url}") private val webappUrl: String,
    @Value("\${wutsi.application.mail.debug}") private val debug: String,
    @Value("\${wutsi.application.mail.whitelist}") private val whitelist: String,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DailyEmailSender::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info(">>> Email Whitelist: $whitelist")
    }

    @Transactional
    fun send(command: SendStoryDailyEmailCommand) {
        logger.add("story_id", command.storyId)
        logger.add("command", "SendStoryDailyEmailCommand")

        val recipientIds = findRecipientIds(command.storyId)

        var delivered = 0
        var failed = 0
        recipientIds.forEach {
            try {
                val payload = send(command.storyId, it)
                if (payload != null) {
                    notify(command.storyId, it, payload)
                    delivered++
                }
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send daily email to User#$it", ex)
                failed++
            }
        }
        logger.add("recipient_count", recipientIds.size)
        logger.add("delivery_count", delivered)
        logger.add("error_count", failed)
    }

    private fun findRecipientIds(storyId: Long): List<Long> {
        val story = storyService.findById(storyId)
        return subscriptionService.findSubscriptions(listOf(story.userId)).map { it.subscriberId }
    }

    private fun send(storyId: Long, recipientId: Long): StoryDailyEmailSentPayload? {
        if (alreadySent(storyId, recipientId)) { // Make sure email never sent more than once!!!
            return null
        }

        val story = storyService.findById(storyId)
        val content = storyService.findContent(story, story.language).getOrNull() ?: return null

        val blog = userService.findById(story.userId)
        val recipient = userService.findById(recipientId)
        val messageId = send(content, blog, recipient) ?: return null
        return StoryDailyEmailSentPayload(
            messageId = messageId,
            email = recipient.email,
        )
    }

    private fun alreadySent(storyId: Long, recipientId: Long): Boolean =
        if (eventStore.events(
                streamId = StreamId.STORY,
                type = STORY_DAILY_EMAIL_SENT_EVENT,
                entityId = storyId.toString(),
                userId = recipientId.toString(),
            ).isNotEmpty()
        ) {
            LOGGER.warn("Daily email already sent to User#$recipientId")
            true
        } else {
            false
        }

    private fun send(content: StoryContentEntity, blog: UserEntity, recipient: UserEntity): String? {
        if (recipient.email.isNullOrEmpty()) {
            return null
        }

        val message = createEmailMessage(content, blog, recipient)
        debug(message)
        if (isWhitelisted(message.recipient.email)) {
            return messagingServiceProvider.get(MessagingType.EMAIL).send(message)
        } else {
            return null
        }
    }

    private fun isWhitelisted(email: String): Boolean =
        if (whitelist.contains(email)) {
            true
        } else {
            LOGGER.warn("$email is not whitelisted")
            false
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

    private fun debug(message: Message) {
        if (debug.toBoolean()) {
            LOGGER.info("Mime-Type: ${message.mimeType}>")
            LOGGER.info("To: ${message.recipient.displayName}< ${message.recipient.email}>")
            LOGGER.info("Subject: ${message.subject}>")
            LOGGER.info("${message.body}\n")
        }
    }

    private fun createMailContext(blog: UserEntity) = MailContext(
        assetUrl = assetUrl,
        websiteUrl = webappUrl,
        template = "default",
        blog = Blog(
            name = blog.name,
            logoUrl = blog.pictureUrl,
            fullName = blog.fullName,
        ),
    )

    private fun notify(storyId: Long, recipientId: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.STORY,
                entityId = storyId.toString(),
                userId = recipientId.toString(),
                type = STORY_DAILY_EMAIL_SENT_EVENT,
                timestamp = Date(),
                payload = payload,
            ),
        )
        eventStream.publish(STORY_DAILY_EMAIL_SENT_EVENT, EventPayload(eventId))
    }
}
