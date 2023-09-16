package com.wutsi.ml.embedding.job

import com.wutsi.ml.embedding.model.author.AuthorTfidfEmbeddingModel
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.stereotype.Service

/**
 * This job is executed by the Heroku scheduler as one-off
 */
@Service
class AuthorTfidfEmbeddingJob(
    private val embedding: AuthorTfidfEmbeddingModel,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "embedding-author-tfidf"

    override fun doRun(): Long =
        embedding.build()
}
