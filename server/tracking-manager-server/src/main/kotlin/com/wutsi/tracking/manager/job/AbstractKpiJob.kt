package com.wutsi.tracking.manager.job

import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.cron.CronLockManager
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.Repository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.StorageInputStreamIterator
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId

abstract class AbstractKpiJob(
    protected val dao: Repository<*>,
    lockManager: CronLockManager,
) : AbstractCronJob(lockManager) {
    @Autowired
    protected lateinit var storage: StorageService

    @Autowired
    protected lateinit var logger: KVLogger

    override fun getJobName() = "compute-reads-kpi"

    abstract fun createAggregator(date: LocalDate): Aggregator<*, *, *>

    override fun doRun(): Long {
        val date = LocalDate.now(ZoneId.of("UTC"))
        logger.add("date", date)

        createAggregator(date).aggregate()
        return 1
    }

    protected open fun createInputStreamIterator(date: LocalDate): StorageInputStreamIterator {
        val urls = mutableListOf<URL>()

        urls.addAll(dao.getURLs(date.minusDays(1)))
        urls.addAll(dao.getURLs(date))

        return StorageInputStreamIterator(urls, storage)
    }
}
