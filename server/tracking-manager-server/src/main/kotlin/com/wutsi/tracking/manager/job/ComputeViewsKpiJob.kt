package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.StorageInputStreamIterator
import com.wutsi.tracking.manager.service.aggregator.views.ViewFilter
import com.wutsi.tracking.manager.service.aggregator.views.ViewMapper
import com.wutsi.tracking.manager.service.aggregator.views.ViewOutputWriter
import com.wutsi.tracking.manager.service.aggregator.views.ViewReducer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class ComputeViewsKpiJob(
    private val dao: TrackRepository,
    private val storage: StorageService,
    private val logger: KVLogger,
    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    override fun getJobName() = "compute-views-kpi"

    @Scheduled(cron = "\${wutsi.application.jobs.compute-views-kpi.cron}")
    override fun run() {
        super.run()
    }

    override fun doRun(): Long {
        val date = LocalDate.now(ZoneId.of("UTC"))
        val inputs = createInputStreamIterator(date)
        val output = createOutputWriter(date)

        logger.add("date", date)
        logger.add("input_urls", inputs.urls)
        logger.add("output_path", output.path)
        Aggregator(
            dao = dao,
            inputs = inputs,
            mapper = ViewMapper(),
            reducer = ViewReducer(),
            output = output,
            filter = ViewFilter(date)
        ).aggregate()
        return 1
    }

    private fun createInputStreamIterator(date: LocalDate): StorageInputStreamIterator {
        val urls = mutableListOf<URL>()

        urls.addAll(dao.getURLs(date.minusDays(1)))
        urls.addAll(dao.getURLs(date))

        return StorageInputStreamIterator(urls, storage)
    }

    private fun createOutputWriter(date: LocalDate): ViewOutputWriter {
        val path = "kpi/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/views.csv"
        return ViewOutputWriter(path, storage)
    }
}
