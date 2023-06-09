package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.reads.ReadFilter
import com.wutsi.tracking.manager.service.aggregator.reads.ReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.ReadOutputWriter
import com.wutsi.tracking.manager.service.aggregator.reads.ReadReducer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ComputeReadsKpiJob(lockManager: CronLockManager) : AbstractKpiJob(lockManager) {
    override fun getJobName() = "compute-reads-kpi"

    override fun createAggregator(date: LocalDate) = Aggregator(
        dao = dao,
        inputs = createInputStreamIterator(date),
        mapper = ReadMapper(),
        reducer = ReadReducer(),
        output = createOutputWriter(date),
        filter = ReadFilter(date),
    )

    @Scheduled(cron = "\${wutsi.application.jobs.compute-reads-kpi.cron}")
    override fun run() {
        super.run()
    }

    private fun createOutputWriter(date: LocalDate): ReadOutputWriter {
        val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/views.csv"
        return ReadOutputWriter(path, storage)
    }
}
