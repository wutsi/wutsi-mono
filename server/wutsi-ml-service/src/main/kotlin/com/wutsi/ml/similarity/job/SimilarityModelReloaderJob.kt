package com.wutsi.ml.similarity.job

import com.wutsi.ml.similarity.dto.SimilarityModelType
import com.wutsi.ml.similarity.model.SimilarityModelFactory
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SimilarityModelReloaderJob(
    private val factory: SimilarityModelFactory,
    private val logger: KVLogger,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "similarity-model-reloader"

    @Scheduled(cron = "\${wutsi.crontab.similarity-model-reloader}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        SimilarityModelType.values()
            .filter { it != SimilarityModelType.UNKNOWN }
            .forEach { type ->
                try {
                    logger.add("type", type)

                    factory.get(type).reload()
                } finally {
                    logger.log()
                }
            }
        return 1L
    }
}
