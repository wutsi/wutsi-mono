package com.wutsi.blog.subscription.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.dao.SubscriptionUserRepository
import com.wutsi.blog.subscription.domain.SubscriptionEntity
import com.wutsi.blog.subscription.domain.SubscriptionUserEntity
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
        log(command)
        if (!isValid(command)) {
            return
        }

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
        logger.add("event_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(EventType.SUBSCRIBED_EVENT, payload)
        eventStream.publish(EventType.SUBSCRIBED_EVENT, payload)
    }

    @Transactional
    fun unsubscribe(command: UnsubscribeCommand) {
        log(command)
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
        logger.add("event_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(EventType.UNSUBSCRIBED_EVENT, payload)
        eventStream.publish(EventType.UNSUBSCRIBED_EVENT, payload)
    }

    @Transactional
    fun onSubscribed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        try {
            val subscription = SubscriptionEntity(
                userId = event.entityId.toLong(),
                subscriberId = event.userId!!.toLong(),
                timestamp = event.timestamp,
            )
            subscriptionDao.save(subscription)
            logger.add("subscription_status", "created")

            updateUser(subscription.userId)
        } catch (ex: DataIntegrityViolationException) {
            LOGGER.warn("Duplicate entry", ex)
            logger.add("subscription_already_created", true)
        }
    }

    @Transactional
    fun onUnsubscribed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val subscription = subscriptionDao.findByUserIdAndSubscriberId(
            userId = event.entityId.toLong(),
            subscriberId = event.userId!!.toLong(),
        )
        if (subscription != null) {
            subscriptionDao.delete(subscription)
            logger.add("subscription_status", "deleted")

            updateUser(subscription.userId)
        }
    }

    private fun updateUser(userId: Long) {
        val opt = userDao.findById(userId)
        if (opt.isEmpty) {
            userDao.save(
                SubscriptionUserEntity(
                    userId = userId,
                    count = subscriptionDao.countByUserId(userId),
                ),
            )
        } else {
            val counter = opt.get()
            counter.count = subscriptionDao.countByUserId(userId)
            userDao.save(counter)
        }
    }

    private fun isValid(command: SubscribeCommand): Boolean {
        if (command.userId == command.subscriberId) {
            logger.add("validation_failure", "self_subscription")
            return false
        }
        return true
    }

    private fun log(command: SubscribeCommand) {
        logger.add("command_user_id", command.userId)
        logger.add("command_subscriber_id", command.subscriberId)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun log(command: UnsubscribeCommand) {
        logger.add("command_user_id", command.userId)
        logger.add("command_subscriber_id", command.subscriberId)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun log(event: Event) {
        logger.add("evt_id", event.id)
        logger.add("evt_type", event.type)
        logger.add("evt_entity_id", event.entityId)
        logger.add("evt_user_id", event.userId)
    }
}
