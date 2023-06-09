package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.views.ViewFilter
import com.wutsi.tracking.manager.service.aggregator.views.ViewMapper
import com.wutsi.tracking.manager.service.aggregator.views.ViewOutputWriter
import com.wutsi.tracking.manager.service.aggregator.views.ViewReducer
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ComputeViewsKpiJob(
    @Value("\${wutsi.application.jobs.compute-views-kpi.enabled}") private val enabled: Boolean,
    lockManager: CronLockManager,
) : AbstractKpiJob(lockManager) {
    override fun getJobName() = "compute-views-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.compute-views-kpi.cron}")
    override fun run() {
        super.run()
    }

    override fun createAggregator(date: LocalDate) = Aggregator(
        dao = dao,
        inputs = createInputStreamIterator(date),
        mapper = ViewMapper(),
        reducer = ViewReducer(),
        output = createOutputWriter(date),
        filter = ViewFilter(date),
    )

    private fun createOutputWriter(date: LocalDate): ViewOutputWriter {
        val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/views.csv"
        return ViewOutputWriter(path, storage)
    }
}
