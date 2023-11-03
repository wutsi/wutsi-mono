package com.wutsi.blog.mail.job

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.mail.service.XEmailService
import com.wutsi.blog.mail.service.ses.SESNotification
import com.wutsi.blog.mail.service.sqs.SQSMessageBody
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.LoggerFactory

abstract class AbstractProcessSESQueueJob(
    private var xemailService: XEmailService,
    private var objectMapper: ObjectMapper,
    private var logger: KVLogger,
    private var sqs: AmazonSQS,
    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    abstract fun queueName(): String

    override fun doRun(): Long {
        logger.add("queue_name", queueName())

        val url = sqs.getQueueUrl(queueName()).queueUrl
        logger.add("queue_url", url)

        val request = ReceiveMessageRequest(url).withMaxNumberOfMessages(10)
        var count = 0L
        var blacklisted = 0L
        while (true) {
            val messages = sqs.receiveMessage(request).messages
            messages.forEach { message ->
                try {
                    count++
                    if (process(message, url)) {
                        blacklisted++
                    }
                } catch (ex: Exception) {
                    LoggerFactory.getLogger(this::class.java).warn("Unable to process message", ex)
                }
            }

            if (messages.isEmpty()) {
                break
            }
        }

        logger.add("message_count", count)
        logger.add("message_blacklisted", blacklisted)
        return count
    }

    private fun process(message: Message, url: String): Boolean {
        // Process
        val body = objectMapper.readValue(message.body, SQSMessageBody::class.java)
        val request = objectMapper.readValue(removeCR(body.message), SESNotification::class.java)
        val result = xemailService.process(request)

        // Delete
        sqs.deleteMessage(
            DeleteMessageRequest(url, message.receiptHandle)
        )
        return result
    }

    private fun removeCR(value: String) = value.replace('\n', ' ')
}
