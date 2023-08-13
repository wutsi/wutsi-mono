package com.wutsi.ml.embedding.job

import com.wutsi.ml.embedding.service.TfIdfEmbeddingService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TfIdfEmbeddingReloadJob(
    private val service: TfIdfEmbeddingService,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "embedding-tfidf-reload"

    @Scheduled(cron = "\${wutsi.crontab.embedding-tfidf-reload}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        service.init()
        return 1L
    }
}
