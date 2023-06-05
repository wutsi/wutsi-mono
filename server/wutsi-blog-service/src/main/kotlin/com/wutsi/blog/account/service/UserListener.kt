package com.wutsi.blog.account.service

import com.wutsi.blog.client.event.LoginEvent
import com.wutsi.blog.client.event.PublishEvent
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.user.domain.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Deprecated("")
@Service
class UserListener(
    private val storyService: StoryService,
    private val userService: UserServiceV0,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UserListener::class.java)
    }

//    @EventListener
//    fun onEvent(event: Event) {
//        LOGGER.info("onEvent(${event.type},...")
//
//        if (event.type == SubscriptionEventType.SUBSCRIPTION_CREATED.urn ||
//            event.type == SubscriptionEventType.SUBSCRIPTION_EXPIRED.urn ||
//            event.type == SubscriptionEventType.SUBSCRIPTION_CANCELLED.urn
//        ) {
//            onSubscriptionChanged(event)
//        }
//    }
//
//    private fun onSubscriptionChanged(event: Event) {
//        try {
//            val payload = event.payloadAs(SubscriptionEventPayload::class.java)
//            val subscription = api.getSubscription(payload.subscriptionId).subscription
//            val userId = subscription.plan.partnerId
//            val count = api.partnerSubscriptionCount(subscription.plan.partnerId, Status.ACTIVE.name).count
//            userService.updateSubscriberCount(userId, count)
//        } catch (ex: Exception) {
//            LOGGER.error("Unable to handle $event", ex)
//        }
//    }

    @Async
    @EventListener
    @Transactional
    fun onPublish(event: PublishEvent) {
        LOGGER.info("onPublish $event")

        try {
            val story = storyService.findById(event.storyId)
            val user = userService.findById(story.userId)

            user.blog = true
            user.lastPublicationDateTime = story.publishedDateTime
            user.storyCount = totalStories(user)
            userService.save(user)
        } catch (ex: Exception) {
            LOGGER.info("Unpexpected error when handling $event", ex)
        }
    }

    @Async
    @EventListener
    fun onLogin(event: LoginEvent) {
        LOGGER.info("onLogin $event")

        try {
            userService.downloadImage(event.userId)
        } catch (ex: Exception) {
            LOGGER.error("Unexpected error when handling $event", ex)
        }
    }

    private fun totalStories(user: UserEntity): Long = storyService.countStories(
        SearchStoryRequest(
            userIds = listOf(user.id!!),
            status = StoryStatus.PUBLISHED,
        ),
    ).toLong()
}
