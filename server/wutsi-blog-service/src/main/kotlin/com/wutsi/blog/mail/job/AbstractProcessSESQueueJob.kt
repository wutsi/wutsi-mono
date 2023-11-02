package com.wutsi.blog.mail.job

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.mail.service.XEmailService
import com.wutsi.blog.mail.service.ses.SESNotification
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
        while (true) {
            val messages = sqs.receiveMessage(request).messages
            messages.forEach { message ->
                LoggerFactory.getLogger(javaClass).info("-----------------\n${message.body}")

                val req = objectMapper.readValue(message.body, SESNotification::class.java)
                if (xemailService.process(req)) {
                    count++
                    sqs.deleteMessage(
                        DeleteMessageRequest(url, message.receiptHandle)
                    )
                }
            }

            break
//            if (messages.isEmpty()) {
//                break
//            }
        }
        return count
    }
}
