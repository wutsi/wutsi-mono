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
import org.springframework.beans.factory.annotation.Value
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
    private val validator: EmailValidatorSet,
    @Value("\${wutsi.application.mail.import.max-rows}") private val maxRows: Int,
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
        var errors = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setDelimiter(",")
                .build(),
        )
        parser.use {
            var imported = 0L
            var row = 1
            for (record in parser) {
                if (row > maxRows) {
                    row > maxRows
                    break
                }

                try {
                    for (i in 0 until record.size()) {
                        val email = record.get(i)?.lowercase()?.trim()
                        if (email.isNullOrEmpty()) {
                            LOGGER.warn("$row - Max rows reached!!!")
                            continue
                        }

                        if (service.isValidEmailAddress(email)) {
                            val failure = validator.validate(email)
                            if (failure == null) {
                                service.subscribe(
                                    command = SubscribeCommand(
                                        userId = command.userId,
                                        email = email,
                                        timestamp = command.timestamp,
                                    ),
                                    sendEvent = false
                                )
                                imported++
                                break
                            } else {
                                errors++
                                LOGGER.warn("$row - $email not imported. Rule=$failure")
                            }
                        }
                    }
                } catch (ex: Exception) {
                    errors++
                    LOGGER.warn("$row - Unexpected error, url=${command.url}", ex)
                } finally {
                    row++
                }
            }

            logger.add("email_count", imported)
            logger.add("error_count", errors)
            return imported
        }
    }

    @Transactional
    fun onImported(payload: EventPayload) {
        val event = eventStore.event(payload.eventId)
        val user = userService.findById(event.entityId.toLong())
        userService.onSubscribed(user)
    }
}
