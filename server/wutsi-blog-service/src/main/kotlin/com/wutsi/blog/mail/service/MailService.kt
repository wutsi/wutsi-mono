package com.wutsi.blog.mail.service

import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class MailService(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val userService: UserService,
    private val subscriptionService: SubscriptionService,
    private val dailyMailSender: DailyMailSender,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailService::class.java)
        private val LIMIT = 100
    }

    fun send(command: SendStoryDailyEmailCommand) {
        logger.add("story_id", command.storyId)
        logger.add("command", "SendStoryDailyEmailCommand")

        // Story
        val story = storyService.findById(command.storyId)
        val content = storyService.findContent(story, story.language).getOrNull() ?: return
        val blog = userService.findById(story.userId)

        var delivered = 0
        var failed = 0
        var offset = 0
        while (true) {
            // Subscribers
            val subscriberIds = subscriptionService.search(
                SearchSubscriptionRequest(
                    userIds = listOf(story.userId),
                    limit = LIMIT,
                    offset = offset,
                ),
            ).map { it.subscriberId }
            if (subscriberIds.isEmpty()) {
                break
            }

            // Recipients
            val recipients = userService.search(
                SearchUserRequest(
                    userIds = subscriberIds,
                    limit = subscriberIds.size,
                ),
            )

            // Send
            recipients.forEach { recipient ->
                try {
                    if (dailyMailSender.send(blog, content, recipient)) {
                        delivered++
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to send daily email to User#${recipient.id}", ex)
                    failed++
                }
            }

            // Next
            if (subscriberIds.size < LIMIT) {
                break
            }
            offset += LIMIT
        }
        logger.add("subscriber_count", blog.subscriberCount)
        logger.add("delivery_count", delivered)
        logger.add("error_count", failed)
    }
}
