package com.wutsi.blog.mail.service

import com.wutsi.blog.SortOrder
import com.wutsi.blog.event.EventType
import com.wutsi.blog.mail.service.sender.blog.WelcomeSubscriberMailSender
import com.wutsi.blog.mail.service.sender.product.EBookLaunchMailSender
import com.wutsi.blog.mail.service.sender.transaction.OrderAbandonedMailSender
import com.wutsi.blog.mail.service.sender.transaction.OrderMailSender
import com.wutsi.blog.product.domain.ProductEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.transaction.domain.TransactionEntity
import com.wutsi.blog.transaction.dto.TransactionType
import com.wutsi.blog.user.dao.UserRepository
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.payment.core.Status
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MailService(
    private val storyService: StoryService,
    private val logger: KVLogger,
    private val userDao: UserRepository,
    private val subscriptionDao: SubscriptionRepository,
    private val orderMailSender: OrderMailSender,
    private val abandonedMailSender: OrderAbandonedMailSender,
    private val eBookLaunchMailSender: EBookLaunchMailSender,
    private val welcomeSubscriberMailSender: WelcomeSubscriberMailSender,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(MailService::class.java)
        private const val LIMIT = 100
    }

    @Deprecated("")
    fun sendAbandonedHourlyEmail(tx: TransactionEntity): String? =
        if (tx.status == Status.FAILED) {
            abandonedMailSender.send(tx, EventType.TRANSACTION_ABANDONED_HOURLY_EMAIL_SENT_EVENT)
        } else {
            null
        }

    @Deprecated("")
    fun sendEBookLaunch(product: ProductEntity) {
        val author = userDao.findById(product.store.userId).get()
        var delivered = 0
        var failed = 0
        var offset = 0
        while (true) {
            // Subscribers
            val subscriberIds = subscriptionDao
                .findByUserId(
                    author.id!!,
                    PageRequest.of(offset / LIMIT, LIMIT)
                ).map { it.subscriberId }
            if (subscriberIds.isEmpty()) {
                break
            }

            // Recipients
            val recipients = userDao.findAllById(subscriberIds)

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

    fun onSubscribed(blogId: Long, subscriberId: Long) {
        try {
            val blog = userDao.findById(blogId).get()
            val recipient = userDao.findById(subscriberId).get()
            val stories = storyService.searchStories(
                SearchStoryRequest(
                    userIds = listOf(blogId),
                    status = StoryStatus.PUBLISHED,
                    sortBy = StorySortStrategy.PUBLISHED,
                    sortOrder = SortOrder.DESCENDING,
                    limit = 5,
                )
            )
            welcomeSubscriberMailSender.send(blog, recipient, stories)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send welcome email", ex)
        }
    }
}
