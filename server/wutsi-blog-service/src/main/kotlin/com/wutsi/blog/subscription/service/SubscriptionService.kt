package com.wutsi.blog.subscription.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.SUBSCRIBED_EVENT
import com.wutsi.blog.event.EventType.UNSUBSCRIBED_EVENT
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
import org.springframework.stereotype.Service
import java.lang.Long.max
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
    @Transactional
    fun subscribe(command: SubscribeCommand) {
        log(command)
        if (!isValid(command)) {
            return
        }
        execute(command)
        notify(SUBSCRIBED_EVENT, command.userId, command.subscriberId, command.timestamp)
    }

    @Transactional
    fun unsubscribe(command: UnsubscribeCommand) {
        log(command)
        if (execute(command)) {
            notify(UNSUBSCRIBED_EVENT, command.userId, command.subscriberId, command.timestamp)
        }
    }

    @Transactional
    fun onSubscribed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val count = updateUser(event.entityId.toLong())
        logger.add("count", count)
    }

    @Transactional
    fun onUnsubscribed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        log(event)

        val count = updateUser(event.entityId.toLong())
        logger.add("count", count)
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

    private fun execute(command: SubscribeCommand) {
        val subscription = SubscriptionEntity(
            userId = command.userId,
            subscriberId = command.subscriberId,
            timestamp = Date(command.timestamp),
        )
        subscriptionDao.save(subscription)
        logger.add("subscription_status", "created")
    }

    private fun log(command: UnsubscribeCommand) {
        logger.add("command_user_id", command.userId)
        logger.add("command_subscriber_id", command.subscriberId)
        logger.add("command_timestamp", command.timestamp)
    }

    private fun execute(command: UnsubscribeCommand): Boolean {
        val subscription = subscriptionDao.findByUserIdAndSubscriberId(
            userId = command.userId,
            subscriberId = command.subscriberId,
        ) ?: return false

        subscriptionDao.delete(subscription)
        logger.add("subscription_status", "deleted")
        return true
    }

    private fun log(event: Event) {
        logger.add("evt_id", event.id)
        logger.add("evt_type", event.type)
        logger.add("evt_entity_id", event.entityId)
        logger.add("evt_user_id", event.userId)
    }

    private fun notify(type: String, userId: Long, subscriberId: Long, timestamp: Long) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.SUBSCRIPTION,
                type = type,
                entityId = userId.toString(),
                userId = subscriberId.toString(),
                timestamp = Date(timestamp),
            ),
        )
        logger.add("event_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, payload)
        eventStream.publish(type, payload)
    }

    private fun updateUser(userId: Long): Long {
        val opt = userDao.findById(userId)
        val count = max(
            0L,
            count(userId, SUBSCRIBED_EVENT) - count(userId, UNSUBSCRIBED_EVENT),
        )
        if (opt.isEmpty) {
            userDao.save(
                SubscriptionUserEntity(
                    userId = userId,
                    count = count,
                ),
            )
        } else {
            val counter = opt.get()
            counter.count = count
            userDao.save(counter)
        }
        return count
    }

    private fun count(userId: Long, type: String): Long =
        eventStore.eventCount(streamId = StreamId.SUBSCRIPTION, entityId = userId.toString(), type = type)
}
