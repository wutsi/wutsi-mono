package com.wutsi.blog.follower.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.follower.dao.SubscriptionRepository
import com.wutsi.blog.follower.dao.SubscriptionUserRepository
import com.wutsi.blog.follower.domain.SubscriptionEntity
import com.wutsi.blog.follower.domain.SubscriptionUserEntity
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.util.Date
import javax.transaction.Transactional

@Service
class SubscriptionService(
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val subscriptionDao: SubscriptionRepository,
    private val userDao: SubscriptionUserRepository,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubscriptionService::class.java)
    }

    @Transactional
    fun subscribe(command: SubscribeCommand) {
        if (!isValid(command)) {
            logger.add("valid", false)
            return
        }

        logger.add("valid", true)
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.SUBSCRIPTION,
                type = EventType.SUBSCRIBE_COMMAND,
                entityId = command.userId.toString(),
                userId = command.subscriberId.toString(),
                payload = command,
                timestamp = Date(command.timestamp),
            ),
        )

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(EventType.SUBSCRIBED_EVENT, payload)
        eventStream.publish(EventType.SUBSCRIBED_EVENT, payload)
    }


    @Transactional
    fun unsubscribe(command: UnsubscribeCommand) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.SUBSCRIPTION,
                type = EventType.UNSUBSCRIBE_COMMAND,
                entityId = command.userId.toString(),
                userId = command.subscriberId.toString(),
                payload = command,
                timestamp = Date(command.timestamp),
            ),
        )

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(EventType.UNSUBSCRIBED_EVENT, payload)
        eventStream.publish(EventType.UNSUBSCRIBED_EVENT, payload)
    }

    private fun isValid(command: SubscribeCommand): Boolean =
        command.userId != command.subscriberId

    @Transactional
    fun onSubscribed(payload: EventPayload) {
        try {
            val event = eventStore.event(payload.eventId)
            val subscription = SubscriptionEntity(
                userId = event.entityId.toLong(),
                subscriberId = event.userId!!.toLong(),
                timestamp = event.timestamp,
            )
            subscriptionDao.save(subscription)
            logger.add("subscription_created", true)

            updateUser(subscription.userId)
        } catch (ex: DataIntegrityViolationException) {
            LOGGER.warn("Duplicate entry", ex)
            logger.add("subscription_already_created", true)
        }
    }

    @Transactional
    fun onUnsubscribed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val subscription = subscriptionDao.findByUserIdAndSubscriberId(
            userId = event.entityId.toLong(),
            subscriberId = event.userId!!.toLong()
        )
        if (subscription != null) {
            subscriptionDao.delete(subscription)
            updateUser(subscription.userId)
        }
    }

    private fun updateUser(userId: Long) {
        val opt = userDao.findById(userId)
        if (opt.isEmpty) {
            userDao.save(
                SubscriptionUserEntity(
                    userId = userId,
                    count = subscriptionDao.countByUserId(userId)
                )
            )
        } else {
            val counter = opt.get()
            counter.count = subscriptionDao.countByUserId(userId)
            userDao.save(counter)
        }
    }
}
