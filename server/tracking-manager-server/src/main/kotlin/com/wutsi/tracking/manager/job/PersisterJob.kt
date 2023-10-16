package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.tracking.manager.service.pipeline.filter.PersisterFilter
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PersisterJob(private val persister: PersisterFilter) {
    @Scheduled(cron = "\${wutsi.application.jobs.persister.cron}")
    fun run() {
        val logger = DefaultKVLogger()
        val count = persister.flush()

        logger.add("count", count)
        logger.log()
    }
}
