package com.wutsi.blog.mail.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

@Service
class MailService(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val userService: UserService,
    private val xemailService: XEmailService,
    private val subscriptionService: SubscriptionService,
    private val dailyMailSender: DailyMailSender,
    private val weeklyMailSender: WeeklyMailSender,
    private val loginLinkSender: LoginLinkSender,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailService::class.java)
        private const val LIMIT = 100
    }

    fun sendDaily(command: SendStoryDailyEmailCommand) {
        logger.add("story_id", command.storyId)
        logger.add("command", "SendStoryDailyEmailCommand")

        // Story
        val story = storyService.findById(command.storyId)
        val content = storyService.findContent(story, story.language).getOrNull() ?: return
        val blog = userService.findById(story.userId)

        var delivered = 0
        var failed = 0
        var offset = 0
        var blacklisted = 0
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
            val otherStories = findOtherStories(story)
            recipients.forEach { recipient ->
                if (recipient.email.isNullOrEmpty()) {
                    // Do nothing
                } else if (xemailService.contains(recipient.email!!)) {
                    blacklisted++
                } else {
                    try {
                        if (dailyMailSender.send(blog, content, recipient, otherStories)) {
                            delivered++
                        }
                    } catch (ex: Exception) {
                        LOGGER.warn("Unable to send daily email to User#${recipient.id}", ex)
                        failed++
                    }
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
        logger.add("blacklist_count", blacklisted)
        logger.add("error_count", failed)
    }

    fun sendWeekly() {
        // Story
        val today = LocalDate.now()
        val stories = storyService.searchStories(
            SearchStoryRequest(
                status = StoryStatus.PUBLISHED,
                activeUserOnly = true,
                publishedStartDate = DateUtils.toDate(today.minusDays(8)),
                publishedEndDate = DateUtils.toDate(today.minusDays(1)),
                limit = 200
            )
        )

        val userIds = stories.map { it.userId }.toSet()
        val users = userService.search(
            SearchUserRequest(
                userIds = userIds.toList(),
                limit = userIds.size
            )
        )

        var recipientCount = 0
        var deliveryCount = 0
        var errorCount = 0
        var offset = 0
        var blacklistCount = 0
        while (true) {
            // Recipients
            val recipients = userService.search(
                SearchUserRequest(
                    limit = LIMIT,
                    offset = offset
                ),
            )
            if (recipients.isEmpty()) {
                break
            }

            // Send
            recipients.forEach { recipient ->
                recipientCount++

                if (recipient.email.isNullOrEmpty()) {
                    // Do nothing
                } else if (xemailService.contains(recipient.email!!)) {
                    blacklistCount++
                } else {
                    try {
                        if (weeklyMailSender.send(stories, users, recipient)) {
                            deliveryCount++
                        }
                    } catch (ex: Exception) {
                        LOGGER.warn("Unable to send weekly email to User#${recipient.id}", ex)
                        errorCount++
                    }
                }
            }

            // Next
            offset += LIMIT
        }
        logger.add("story_count", stories.size)
        logger.add("recipient_count", recipientCount)
        logger.add("delivery_count", deliveryCount)
        logger.add("blacklist_count", blacklistCount)
        logger.add("error_count", errorCount)
    }

    fun sendLoginLink(event: EventPayload) {
        loginLinkSender.send(event)
    }

    private fun findOtherStories(story: StoryEntity): List<StoryEntity> =
        storyService.searchStories(
            SearchStoryRequest(
                userIds = listOf(story.userId),
                sortBy = StorySortStrategy.PUBLISHED,
                status = StoryStatus.PUBLISHED,
                limit = 10,
            ),
        ).filter { it.id != story.id }
}
