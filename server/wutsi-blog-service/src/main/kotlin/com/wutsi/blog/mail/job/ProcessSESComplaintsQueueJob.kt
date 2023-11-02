package com.wutsi.blog.mail.job

import com.amazonaws.services.sqs.AmazonSQS
import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.mail.service.XEmailService
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    value = ["wutsi.application.mail.sqs-notification.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class ProcessSESComplaintsQueueJob(
    xemailService: XEmailService,
    objectMapper: ObjectMapper,
    logger: KVLogger,
    sqs: AmazonSQS,
    lockManager: CronLockManager,
    registry: CronJobRegistry,

    @Value("\${wutsi.applicatiom.mail.sqs-notification.queues.complaints-queue-name}") private val queue: String,
) : AbstractProcessSESQueueJob(xemailService, objectMapper, logger, sqs, lockManager, registry) {
    override fun queueName() = queue

    override fun getJobName() = "ses-complaints-processor"

    @Scheduled(cron = "\${wutsi.crontab.ses-complaints-processor}")
    override fun run() {
        super.run()
    }
}
