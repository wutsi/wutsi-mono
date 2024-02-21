package com.wutsi.blog.mail.service

import com.wutsi.blog.SortOrder
import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.mail.service.sender.product.EBookLaunchMailSender
import com.wutsi.blog.mail.service.sender.story.DailyMailSender
import com.wutsi.blog.mail.service.sender.story.WeeklyMailSender
import com.wutsi.blog.mail.service.sender.transaction.OrderAbandonedMailSender
import com.wutsi.blog.mail.service.sender.transaction.OrderMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductService
import com.wutsi.blog.product.service.StoreService
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.service.SubscriptionService
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

@Service
class MailService(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val userService: UserService,
    private val storeService: StoreService,
    private val xemailService: XEmailService,
    private val subscriptionService: SubscriptionService,
    private val productService: ProductService,
    private val dailyMailSender: DailyMailSender,
    private val weeklyMailSender: WeeklyMailSender,
    private val orderMailSender: OrderMailSender,
    private val abandonedMailSender: OrderAbandonedMailSender,
    private val eBookLaunchMailSender: EBookLaunchMailSender,
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
            val store = findStore(blog)
            val products = store?.let { findProducts(story, store) } ?: emptyList()
            recipients.forEach { recipient ->
                if (recipient.email.isNullOrEmpty()) {
                    // Do nothing
                } else if (xemailService.contains(recipient.email!!)) {
                    blacklisted++
                } else {
                    try {
                        if (dailyMailSender.send(blog, store, content, recipient, otherStories, products)) {
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
        val products = findProducts()
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
                        if (weeklyMailSender.send(stories, users, recipient, products)) {
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

    fun onTransactionSuccessful(tx: TransactionEntity) {
        if (tx.type == TransactionType.CHARGE && tx.status == Status.SUCCESSFUL) {
            try {
                val messageId = orderMailSender.send(tx)
                logger.add("message_id", messageId)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send transaction receipt to ${tx.email}", ex)
            }
        }
    }

    fun sendAbandonedHourlyEmail(tx: TransactionEntity): String? =
        if (tx.status == Status.FAILED) {
            abandonedMailSender.send(tx, EventType.TRANSACTION_ABANDONED_HOURLY_EMAIL_SENT_EVENT)
        } else {
            null
        }

    fun sendAbandonedDailyEmail(tx: TransactionEntity): String? =
        if (tx.status == Status.FAILED) {
            abandonedMailSender.send(tx, EventType.TRANSACTION_ABANDONED_DAILY_EMAIL_SENT_EVENT)
        } else {
            null
        }

    fun sendAbandonedWeeklyEmail(tx: TransactionEntity): String? =
        if (tx.status == Status.FAILED) {
            abandonedMailSender.send(tx, EventType.TRANSACTION_ABANDONED_WEEKLY_EMAIL_SENT_EVENT)
        } else {
            null
        }

    fun sendBookLaunch(product: ProductEntity) {
        val author = userService.findById(product.store.userId)
        var delivered = 0
        var failed = 0
        var offset = 0
        while (true) {
            // Subscribers
            val subscriberIds = subscriptionService.search(
                SearchSubscriptionRequest(
                    userIds = listOf(author.id!!),
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
                    if (eBookLaunchMailSender.send(product, author, recipient)) {
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
        logger.add("subscriber_count", author.subscriberCount)
        logger.add("delivery_count", delivered)
        logger.add("error_count", failed)
    }

    private fun findOtherStories(story: StoryEntity): List<StoryEntity> =
        try {
            storyService.searchStories(
                SearchStoryRequest(
                    userIds = listOf(story.userId),
                    sortBy = StorySortStrategy.PUBLISHED,
                    status = StoryStatus.PUBLISHED,
                    limit = 10,
                ),
            ).filter { it.id != story.id }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find other stories", ex)
            emptyList()
        }

    private fun findStore(blog: UserEntity): StoreEntity? =
        blog.storeId?.let { storeId ->
            try {
                storeService.findById(storeId)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to find other stories", ex)
                null
            }
        }

    private fun findProducts(story: StoryEntity, store: StoreEntity): List<ProductEntity> =
        try {
            productService.searchProducts(
                SearchProductRequest(
                    storyId = story.id,
                    storeIds = listOf(store.id ?: ""),
                    sortBy = ProductSortStrategy.ORDER_COUNT,
                    sortOrder = SortOrder.DESCENDING,
                    status = ProductStatus.PUBLISHED,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find products", ex)
            emptyList()
        }

    private fun findProducts(): List<ProductEntity> =
        try {
            productService.searchProducts(
                SearchProductRequest(
                    sortBy = ProductSortStrategy.ORDER_COUNT,
                    sortOrder = SortOrder.DESCENDING,
                    status = ProductStatus.PUBLISHED,
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find products", ex)
            emptyList()
        }
}
