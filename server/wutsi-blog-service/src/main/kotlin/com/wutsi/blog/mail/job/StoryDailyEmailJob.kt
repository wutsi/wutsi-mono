package com.wutsi.blog.mail.job

import com.wutsi.blog.SortOrder
import com.wutsi.blog.mail.dto.SendStoryDailyEmailCommand
import com.wutsi.blog.mail.service.XEmailService
import com.wutsi.blog.mail.service.sender.story.DailyMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.domain.StoreEntity
import com.wutsi.blog.product.dto.ProductSortStrategy
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.SearchProductContext
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
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date
import kotlin.jvm.optionals.getOrNull

@Service
class StoryDailyEmailJob(
    private val sender: DailyMailSender,
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val clock: Clock,
    private val xemailService: XEmailService,
    private val storeService: StoreService,
    private val productService: ProductService,
    private val subscriptionService: SubscriptionService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
    private val userService: UserService,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryDailyEmailJob::class.java)
        private const val LIMIT = 100
    }

    override fun getJobName() = "mail-daily"

    @Scheduled(cron = "\${wutsi.crontab.mail-daily}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val today = DateUtils.toLocalDate(Date(clock.millis()))
        val yesterday = DateUtils.toDate(today.minusDays(1))
        logger.add("date", yesterday)

        val stories = storyService.searchStories(
            SearchStoryRequest(
                sortBy = StorySortStrategy.POPULARITY,
                sortOrder = SortOrder.DESCENDING,
                status = StoryStatus.PUBLISHED,
                publishedStartDate = yesterday,
                limit = 100,
            ),
        )
        logger.add("story_count", stories.size)

        stories.forEach { story ->
            try {
                sendDaily(SendStoryDailyEmailCommand(storyId = story.id ?: -1))
                storyService.onDailyEmailSent(story)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to send the daily email for Story#${story.id}", ex)
            }
        }
        return stories.size.toLong()
    }

    private fun sendDaily(command: SendStoryDailyEmailCommand) {
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
                    offset = offset
                )
            ).map { it.subscriberId }
            if (subscriberIds.isEmpty()) {
                break
            }

            // Recipients
            val recipients = userService.findByIds(subscriberIds)

            // Send
            val otherStories = findOtherStories(story)
            val store = findStore(blog)
            recipients.forEach { recipient ->
                if (recipient.email.isNullOrEmpty()) {
                    // Do nothing
                } else if (xemailService.contains(recipient.email!!)) {
                    blacklisted++
                } else {
                    val products = store?.let { findProducts(story, store, recipient) } ?: emptyList()

                    try {
                        if (sender.send(blog, store, content, recipient, otherStories, products)) {
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

    private fun findOtherStories(story: StoryEntity): List<StoryEntity> =
        try {
            storyService
                .searchStories(
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
        blog.storeId?.let { storeId -> storeService.findById(storeId) }

    private fun findProducts(
        story: StoryEntity,
        store: StoreEntity,
        recipient: UserEntity,
    ): List<ProductEntity> =
        try {
            productService.searchProducts(
                SearchProductRequest(
                    storeIds = listOf(store.id ?: ""),
                    sortBy = ProductSortStrategy.RECOMMENDED,
                    status = ProductStatus.PUBLISHED,
                    excludePurchasedProduct = true,
                    available = true,
                    searchContext = SearchProductContext(
                        storyId = story.id,
                        userId = recipient.id
                    )
                ),
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find products", ex)
            emptyList()
        }
}
