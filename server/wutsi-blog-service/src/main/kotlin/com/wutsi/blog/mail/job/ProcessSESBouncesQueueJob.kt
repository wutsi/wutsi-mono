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
    value = ["wutsi.platform.aws.sqs.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class ProcessSESBouncesQueueJob(
    xemailService: XEmailService,
    objectMapper: ObjectMapper,
    logger: KVLogger,
    sqs: AmazonSQS,
    lockManager: CronLockManager,
    registry: CronJobRegistry,

    @Value("\${wutsi.applicatiom.mail.bounces-queue-name}") private val queue: String,
) : AbstractProcessSESQueueJob(xemailService, objectMapper, logger, sqs, lockManager, registry) {
    override fun queueName() = queue

    override fun getJobName() = "ses-bounces-processor"

    @Scheduled(cron = "\${wutsi.crontab.ses-bounces-processor}")
    override fun run() {
        super.run()
    }
}
