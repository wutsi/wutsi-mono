package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.ReadOutputWriter
import com.wutsi.tracking.manager.service.aggregator.reads.ReadReducer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ComputeDailyReadsKpiJob(
    dao: TrackRepository,
    lockManager: CronLockManager,
) : AbstractKpiJob(dao, lockManager) {
    override fun getJobName() = "compute-daily-reads-kpi"

    override fun createAggregator(date: LocalDate) = Aggregator(
        dao = dao as TrackRepository,
        inputs = createInputStreamIterator(date),
        mapper = DailyReadMapper(),
        reducer = ReadReducer(),
        output = createOutputWriter(date),
        filter = DailyReadFilter(date),
    )

    @Scheduled(cron = "\${wutsi.application.jobs.compute-daily-reads-kpi.cron}")
    override fun run() {
        super.run()
    }

    private fun createOutputWriter(date: LocalDate): ReadOutputWriter {
        val path = "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv"
        return ReadOutputWriter(path, storage)
    }
}
