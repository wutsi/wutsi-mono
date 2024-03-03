package com.wutsi.blog.ads.job

import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsService
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class RunAdsJob(
    private val service: AdsService,
    private val logger: KVLogger,
    private val clock: Clock,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    override fun getJobName() = "ads-run"

    @Scheduled(cron = "\${wutsi.crontab.ads-run}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val now = Date(clock.millis())
        logger.add("date", now)

        var offset = 0
        var count = 0L
        while (true) {
            val ads = service.searchAds(
                SearchAdsRequest(
                    status = listOf(AdsStatus.PUBLISHED),
                    startDateTo = now,
                    limit = 100,
                    offset = offset
                ),
            )
            if (ads.isEmpty()) {
                break
            }

            ads.forEach { ads ->
                if (service.start(ads)) {
                    count++
                }
            }

            offset += ads.size
        }

        return count
    }
}
