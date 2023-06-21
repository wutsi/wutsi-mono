package com.wutsi.blog.mail.service

import com.wutsi.blog.SortOrder
import com.wutsi.blog.event.EventType.SEND_STORY_DAILY_EMAIL_COMMAND
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus.PUBLISHED
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import kotlin.jvm.optionals.getOrNull

@Service
class MailService(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val eventStream: EventStream,
    private val userService: UserService,
    private val subscriptionService: SubscriptionService,
    private val dailyMailSender: DailyMailSender,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailService::class.java)
        private const val LIMIT = 50
    }

    fun sendDailyEmail(): Long {
        val yesterday = DateUtils.toDate(LocalDate.now(ZoneId.of("UTC")).minusDays(1))
        logger.add("date", yesterday)
        logger.add("command", "SendSDailyEmailCommand")

        val stories = storyService.searchStories(
            SearchStoryRequest(
                sortBy = StorySortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                status = PUBLISHED,
                publishedStartDate = yesterday,
                limit = 100,
            ),
        )
        logger.add("story_count", stories.size)

        stories.forEach {
            eventStream.enqueue(
                type = SEND_STORY_DAILY_EMAIL_COMMAND,
                payload = SendStoryDailyEmailCommand(
                    storyId = it.id!!,
                ),
            )
        }
        return stories.size.toLong()
    }

    fun send(command: SendStoryDailyEmailCommand) {
        logger.add("story_id", command.storyId)
        logger.add("command", "SendStoryDailyEmailCommand")

        // Story
        val story = storyService.findById(command.storyId)
        val content = storyService.findContent(story, story.language).getOrNull() ?: return
        val blog = userService.findById(story.userId)

        // Send
        var delivered = 0
        var failed = 0
        var offset = 0
        val recipientIds = findRecipientIds(command.storyId)
        logger.add("recipient_count", recipientIds.size)

        if (recipientIds.isNotEmpty()) {
            while (true) {
                val userIds = recipientIds.subList(
                    offset,
                    Integer.min(offset + LIMIT, recipientIds.size - 1),
                )
                val recipients = userService.search(
                    SearchUserRequest(
                        userIds = userIds,
                        limit = userIds.size,
                    ),
                )

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

                offset += LIMIT
                if (offset >= recipientIds.size) {
                    break
                }
            }
        }

        logger.add("delivery_count", delivered)
        logger.add("error_count", failed)
    }

    private fun findRecipientIds(storyId: Long): List<Long> {
        val story = storyService.findById(storyId)
        return subscriptionService.search(
            SearchSubscriptionRequest(
                userIds = listOf(story.userId),
            ),
        ).map { it.subscriberId }
    }
}
