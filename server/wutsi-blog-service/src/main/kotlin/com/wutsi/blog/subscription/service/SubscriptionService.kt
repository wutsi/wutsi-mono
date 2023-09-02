package com.wutsi.blog.subscription.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.SUBSCRIBED_EVENT
import com.wutsi.blog.event.EventType.SUBSCRIBER_IMPORTED_EVENT
import com.wutsi.blog.event.EventType.SUBSCRIBE_COMMAND
import com.wutsi.blog.event.EventType.UNSUBSCRIBED_EVENT
import com.wutsi.blog.event.StreamId
import com.wutsi.blog.story.service.ReaderService
import com.wutsi.blog.subscription.dao.SearchSubscriptionQueryBuilder
import com.wutsi.blog.subscription.dao.SubscriptionRepository
import com.wutsi.blog.subscription.domain.SubscriptionEntity
import com.wutsi.blog.subscription.dto.ImportSubscriberCommand
import com.wutsi.blog.subscription.dto.SearchSubscriptionRequest
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.SubscribedEventPayload
import com.wutsi.blog.subscription.dto.SubscriberImportedEventPayload
import com.wutsi.blog.subscription.dto.UnsubscribeCommand
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.Predicates
import com.wutsi.event.store.Event
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.stream.EventStream
import jakarta.mail.internet.InternetAddress
import jakarta.persistence.EntityManager
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Date

@Service
class SubscriptionService(
    private val eventStore: EventStore,
    private val eventStream: EventStream,
    private val subscriptionDao: SubscriptionRepository,
    private val userService: UserService,
    private val readerService: ReaderService,
    private val logger: KVLogger,
    private val em: EntityManager,
    private val storage: StorageService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubscriptionService::class.java)
    }

    @Transactional
    fun import(command: ImportSubscriberCommand) {
        logger.add("request_url", command.url)
        logger.add("request_user_id", command.userId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "ImportSubscriberCommand")

        // Download the file
        execute(command)
        notify(
            SUBSCRIBER_IMPORTED_EVENT,
            command.userId,
            null,
            command.timestamp,
            SubscriberImportedEventPayload(command.url),
        )
    }

    private fun execute(command: ImportSubscriberCommand): Long {
        // Download the file
        val file = File.createTempFile("import", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            storage.get(URL(command.url), fout)
        }

        // Read CSV
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .build(),
        )
        parser.use {
            var result = 0L
            for (record in parser) {
                try {
                    var i = 0
                    for (i in 0 until record.size()) {
                        val value = record.get(i)
                        if (isValidEmailAddress(value)) {
                            eventStream.enqueue(
                                SUBSCRIBE_COMMAND,
                                SubscribeCommand(
                                    userId = command.userId,
                                    email = value,
                                    timestamp = command.timestamp,
                                ),
                            )
                            result++
                        }
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unexpected error", ex)
                }
            }
            return result
        }
    }

    private fun isValidEmailAddress(email: String?): Boolean =
        try {
            val emailAddr = InternetAddress(email)
            emailAddr.validate()
            true
        } catch (ex: Exception) {
            false
        }

    @Transactional
    fun subscribe(command: SubscribeCommand) {
        logger.add("request_user_id", command.userId)
        logger.add("request_subscriber_id", command.subscriberId)
        logger.add("request_email", command.email)
        logger.add("request_story_id", command.storyId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "SubscribeCommand")

        if (isValid(command) && execute(command)) {
            notify(
                SUBSCRIBED_EVENT,
                command.userId,
                command.subscriberId,
                command.timestamp,
                SubscribedEventPayload(email = command.email, storyId = command.storyId),
            )
        }
    }

    @Transactional
    fun unsubscribe(command: UnsubscribeCommand) {
        logger.add("request_user_id", command.userId)
        logger.add("request_subscriber_id", command.subscriberId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "UnsubscribeCommand")

        if (execute(command)) {
            notify(UNSUBSCRIBED_EVENT, command.userId, command.subscriberId, command.timestamp)
        }
    }

    @Transactional
    fun onSubscribed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val user = userService.findById(event.entityId.toLong())
        userService.onSubscribed(user)

        val eventPayload = event.payload
        if (eventPayload is SubscribedEventPayload && eventPayload.storyId != null && event.userId != null) {
            readerService.onSubscribed(event.userId!!.toLong(), eventPayload.storyId!!)
        }
    }

    @Transactional
    fun onUnsubscribed(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val user = userService.findById(event.entityId.toLong())
        userService.onUnsubscribed(user)
    }

    fun search(request: SearchSubscriptionRequest): List<SubscriptionEntity> {
        val builder = SearchSubscriptionQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, SubscriptionEntity::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<SubscriptionEntity>
    }

    private fun isValid(command: SubscribeCommand): Boolean {
        if (command.userId == command.subscriberId) {
            logger.add("validation_failure", "self_subscription")
            return false
        }

        if (command.email != null && !isValidEmailAddress(command.email)) {
            logger.add("validation_failure", "invalid_email")
            return false
        }

        return true
    }

    private fun execute(command: SubscribeCommand): Boolean {
        val subscriberId = findSubscriberId(command.email) ?: command.subscriberId
        if (
            subscriberId == command.userId ||
            subscriptionDao.findByUserIdAndSubscriberId(command.userId, subscriberId) != null
        ) {
            return false
        }

        val subscription = SubscriptionEntity(
            userId = command.userId,
            storyId = command.storyId,
            subscriberId = subscriberId,
            timestamp = Date(command.timestamp),
        )
        subscriptionDao.save(subscription)
        logger.add("subscription_status", "created")
        return true
    }

    private fun findSubscriberId(email: String?): Long? =
        email?.let { userService.findByEmailOrCreate(email).id }

    private fun execute(command: UnsubscribeCommand): Boolean {
        val subscription = subscriptionDao.findByUserIdAndSubscriberId(
            userId = command.userId,
            subscriberId = command.subscriberId,
        ) ?: return false

        subscriptionDao.delete(subscription)
        logger.add("subscription_status", "deleted")
        return true
    }

    private fun notify(type: String, userId: Long, subscriberId: Long?, timestamp: Long, payload: Any? = null) {
        val eventId = eventStore.store(
            Event(
                streamId = StreamId.SUBSCRIPTION,
                type = type,
                entityId = userId.toString(),
                userId = subscriberId?.toString(),
                timestamp = Date(timestamp),
                payload = payload,
            ),
        )
        logger.add("event_id", eventId)

        val payload = EventPayload(eventId = eventId)
        eventStream.enqueue(type, payload)
        eventStream.publish(type, payload)
    }
}
