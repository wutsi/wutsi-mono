package com.wutsi.blog.mail.job

import com.wutsi.blog.mail.service.XEmailService
import com.wutsi.blog.mail.service.sender.story.WeeklyMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.product.dto.ProductStatus
import com.wutsi.blog.product.dto.ProductType
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.transaction.dto.SearchTransactionRequest
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.transaction.service.TransactionService
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StoryWeeklyEmailJob(
    private val sender: WeeklyMailSender,
    private val storyService: StoryService,
    private val userService: UserService,
    private val xemailService: XEmailService,
    private val transactionService: TransactionService,
    private val logger: KVLogger,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(StoryWeeklyEmailJob::class.java)
        private const val LIMIT = 100
        private const val MIN_SUBSCRIBER_COUNT = 10
    }

    override fun getJobName() = "mail-weekly"

    @Scheduled(cron = "\${wutsi.crontab.mail-weekly}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        try {
            sendWeekly()
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send the weekly email", ex)
        }
        return 1
    }

    private fun sendWeekly() {
        // Stories of the last week
        val today = LocalDate.now()
        val stories = findStories(today)
        if (stories.isEmpty()) {
            return
        }

        // Filter user's having enough subscribers
        val users = userService.search(
            SearchUserRequest(
                userIds = stories.map { it.userId }.toSet().toList(),
                limit = stories.size,
                minSubscriberCount = MIN_SUBSCRIBER_COUNT
            )
        )

        // Filter out stories from users not having enough subscribers
        val userIds = users.map { user -> user.id }
        val xstories = stories.filter { story -> userIds.contains(story.userId) }
        if (xstories.isEmpty()) {
            return
        }

        // Send
        var recipientCount = 0
        var deliveryCount = 0
        var errorCount = 0
        var offset = 0
        var blacklistCount = 0
        val products = findProducts(today)
        while (true) {
            // Recipients
            val recipients = userService.search(
                SearchUserRequest(
                    limit = LIMIT,
                    offset = offset
                )
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
                        if (sender.send(xstories, users, recipient, products)) {
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

    private fun findProducts(today: LocalDate): List<ProductEntity> =
        try {
            val txs = transactionService.search(
                SearchTransactionRequest(
                    types = listOf(TransactionType.CHARGE),
                    statuses = listOf(Status.SUCCESSFUL),
                    creationDateTimeFrom = DateUtils.toDate(today.minusDays(8)),
                    creationDateTimeTo = DateUtils.toDate(today.minusDays(1)),
                    limit = 20,
                )
            )
            val productMap = txs.groupBy { tx -> tx.product?.id }

            txs.groupBy { tx -> tx.product }
                .mapNotNull { it.key }
                .filter { product ->
                    (product.type == ProductType.EBOOK || product.type == ProductType.COMICS) &&
                        product.available &&
                        product.status == ProductStatus.PUBLISHED
                }
                .sortedWith(
                    object : Comparator<ProductEntity> {
                        override fun compare(o1: ProductEntity, o2: ProductEntity): Int {
                            val sales1 = productMap[o1.id]?.size ?: 0
                            val sales2 = productMap[o2.id]?.size ?: 0
                            return sales2 - sales1
                        }
                    }
                )
                .distinctBy { product -> product.store.id }
                .take(10)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to find products", ex)
            emptyList()
        }

    private fun findStories(today: LocalDate): List<StoryEntity> =
        storyService.searchStories(
            SearchStoryRequest(
                status = StoryStatus.PUBLISHED,
                activeUserOnly = true,
                publishedStartDate = DateUtils.toDate(today.minusDays(8)),
                publishedEndDate = DateUtils.toDate(today.minusDays(1)),
                limit = 200
            )
        )
}
