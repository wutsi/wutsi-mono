package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.StorageInputStreamIterator
import com.wutsi.tracking.manager.service.aggregator.reads.MonthlyReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.ReadOutputWriter
import com.wutsi.tracking.manager.service.aggregator.reads.ReadReducer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ComputeMonthlyReadsKpiJob(
    dao: DailyReadRepository,
    lockManager: CronLockManager,
) : AbstractKpiJob(dao, lockManager) {
    override fun getJobName() = "compute-monthly-reads-kpi"

    override fun createAggregator(date: LocalDate) = Aggregator(
        dao = dao as DailyReadRepository,
        inputs = createInputStreamIterator(date),
        mapper = MonthlyReadMapper(),
        reducer = ReadReducer(),
        output = createOutputWriter(date),
    )

    @Scheduled(cron = "\${wutsi.application.jobs.compute-monthly-reads-kpi.cron}")
    override fun run() {
        super.run()
    }

    private fun createOutputWriter(date: LocalDate): ReadOutputWriter {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv"
        return ReadOutputWriter(path, storage)
    }

    override fun createInputStreamIterator(date: LocalDate): StorageInputStreamIterator {
        val urls = mutableListOf<URL>()
        var cur = LocalDate.of(date.year, date.month, 1)
        val today = LocalDate.now()
        while (true) {
            urls.addAll(dao.getURLs(cur))

            cur = cur.plusDays(1)
            if (cur.month != date.month || cur.isAfter(today)) {
                break
            }
        }
        return StorageInputStreamIterator(urls, storage)
    }

}
