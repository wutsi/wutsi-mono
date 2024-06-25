package com.wutsi.blog.ads.job

import com.wutsi.blog.ads.dto.AdsStatus
import com.wutsi.blog.ads.dto.SearchAdsRequest
import com.wutsi.blog.ads.service.AdsService
import com.wutsi.blog.util.DateUtils
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronJobRegistry
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.util.Date

@Service
class CompleteAdsJob(
    private val service: AdsService,
    private val logger: KVLogger,
    private val clock: Clock,

    lockManager: CronLockManager,
    registry: CronJobRegistry,
) : AbstractCronJob(lockManager, registry) {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CompleteAdsJob::class.java)
    }

    override fun getJobName() = "ads-complete"

    @Scheduled(cron = "\${wutsi.crontab.ads-complete}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val yesterday = DateUtils.addDays(Date(clock.millis()), -1)
        logger.add("date", yesterday)

        var offset = 0
        var count = 0L
        var errors = 0L
        while (true) {
            val ads = service.searchAds(
                SearchAdsRequest(
                    status = listOf(AdsStatus.RUNNING),
                    endDateTo = yesterday,
                    limit = 100,
                    offset = offset
                ),
            )
            if (ads.isEmpty()) {
                break
            }

            ads.forEach {
                try {
                    if (service.complete(it)) {
                        count++
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to complete Ads#${it.id}", ex)
                    errors++
                }
            }

            offset += ads.size
        }

        logger.add("errors", errors)
        logger.add("started", count)
        return count
    }
}
