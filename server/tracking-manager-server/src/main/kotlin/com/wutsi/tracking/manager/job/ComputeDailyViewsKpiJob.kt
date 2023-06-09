package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.views.DailyViewFilter
import com.wutsi.tracking.manager.service.aggregator.views.DailyViewMapper
import com.wutsi.tracking.manager.service.aggregator.views.ViewOutputWriter
import com.wutsi.tracking.manager.service.aggregator.views.ViewReducer
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ComputeDailyViewsKpiJob(
    @Value("\${wutsi.application.jobs.compute-daily-views-kpi.enabled}") private val enabled: Boolean,

    dao: TrackRepository,
    lockManager: CronLockManager,
) : AbstractKpiJob(dao, lockManager) {
    override fun getJobName() = "compute-daily-views-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.compute-daily-views-kpi.cron}")
    override fun run() {
        logger.add("enabled", enabled)
        if (!enabled) {
            return
        }
        super.run()
    }

    override fun createAggregator(date: LocalDate) = Aggregator(
        dao = dao as TrackRepository,
        inputs = createInputStreamIterator(date),
        mapper = DailyViewMapper(),
        reducer = ViewReducer(),
        output = createOutputWriter(date),
        filter = DailyViewFilter(date),
    )

    private fun createOutputWriter(date: LocalDate): ViewOutputWriter {
        val path = "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/views.csv"
        return ViewOutputWriter(path, storage)
    }
}
