package com.wutsi.blog.subscription.service

import com.wutsi.blog.event.EventPayload
import com.wutsi.blog.event.EventType.SUBSCRIBER_IMPORTED_EVENT
import com.wutsi.blog.subscription.dto.ImportSubscriberCommand
import com.wutsi.blog.subscription.dto.SubscribeCommand
import com.wutsi.blog.subscription.dto.SubscriberImportedEventPayload
import com.wutsi.blog.user.service.UserService
import com.wutsi.event.store.EventStore
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Service
class SubscriptionImporterService(
    private val eventStore: EventStore,
    private val userService: UserService,
    private val logger: KVLogger,
    private val storage: StorageService,
    private val service: SubscriptionService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SubscriptionImporterService::class.java)
    }

    fun import(command: ImportSubscriberCommand) {
        logger.add("request_url", command.url)
        logger.add("request_user_id", command.userId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("command", "ImportSubscriberCommand")

        // Download the file
        execute(command)
        service.notify(
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
            var row = 1
            for (record in parser) {
                try {
                    for (i in 0 until record.size()) {
                        val value = record.get(i)?.lowercase()?.trim()
                        if (service.isValidEmailAddress(value)) {
                            service.subscribe(
                                command = SubscribeCommand(
                                    userId = command.userId,
                                    email = value,
                                    timestamp = command.timestamp,
                                ),
                                sendEvent = false
                            )
                            result++
                            break
                        }
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("$row - Unexpected error, url=${command.url}", ex)
                } finally {
                    logger.log()
                    row++
                }
            }
            return result
        }
    }

    @Transactional
    fun onImported(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val user = userService.findById(event.entityId.toLong())
        userService.onSubscribed(user)
    }
}
