package com.wutsi.ml.embedding.job

import com.wutsi.ml.embedding.model.story.StoryTfidfEmbeddingModel
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.stereotype.Service

/**
 * This job is executed by the Heroku scheduler as one-off
 */
@Service
class StoryTfIdfEmbeddingJob(
    private val embedding: StoryTfidfEmbeddingModel,
    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "embedding-story-tfidf"

    override fun doRun(): Long =
        embedding.build()
}
