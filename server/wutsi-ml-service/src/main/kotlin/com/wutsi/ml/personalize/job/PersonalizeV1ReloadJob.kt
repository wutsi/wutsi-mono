package com.wutsi.ml.personalize.job

import com.wutsi.ml.personalize.service.PersonalizeV1Service
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PersonalizeV1ReloadJob(
    private val service: PersonalizeV1Service,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "personalize-v1-reload"

    @Scheduled(cron = "\${wutsi.crontab.personalize-v1-reload}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        service.init()
        return 1L
    }
}
