package com.wutsi.tracking.manager.service

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.Repository
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.StorageInputStreamIterator
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.MonthlyReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.ReadOutputWriter
import com.wutsi.tracking.manager.service.aggregator.reads.ReadReducer
import com.wutsi.tracking.manager.service.aggregator.views.DailyViewFilter
import com.wutsi.tracking.manager.service.aggregator.views.DailyViewMapper
import com.wutsi.tracking.manager.service.aggregator.views.ViewOutputWriter
import com.wutsi.tracking.manager.service.aggregator.views.ViewReducer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
public class KpiService(
    private val storage: StorageService,
    private val trackDao: TrackRepository,
    private val dailyReadDao: DailyReadRepository,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiService::class.java)
    }

    fun computeDailyViews(date: LocalDate) {
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyViewMapper(),
            reducer = ViewReducer(),
            output = ViewOutputWriter(getDailyKpiOutputPath(date, "views.csv"), storage),
            filter = DailyViewFilter(date),
        ).aggregate()
    }

    fun computeDailyReads(date: LocalDate) {
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyReadMapper(),
            reducer = ReadReducer(),
            output = ReadOutputWriter(getDailyKpiOutputPath(date, "reads.csv"), storage),
            filter = DailyReadFilter(date),
        ).aggregate()
    }

    fun computeMonthlyReads(date: LocalDate) {
        Aggregator(
            dao = dailyReadDao,
            inputs = createMonthlyInputStreamIterator(date, dailyReadDao),
            mapper = MonthlyReadMapper(),
            reducer = ReadReducer(),
            output = ReadOutputWriter(getMonthlyKpiOutputPath(date, "reads.csv"), storage),
        ).aggregate()
    }

    fun replay(year: Int, month: Int?) {
        val now = LocalDate.now()

        // Daily KPIs
        var date = LocalDate.of(year, month ?: 1, 1)
        while (true) {
            LOGGER.info("Replay Daily $date")
            replayDaily(date)

            date = date.plusDays(1)
            if (date.isAfter(now) || date.year > year || (month != null && date.month.value > month)) {
                break
            }
        }

        // Monthly KPIs
        date = LocalDate.of(year, month ?: 1, 1)
        while (true) {
            LOGGER.info("Replay Monthly $date")
            replayMonthly(date)

            date = date.plusMonths(1)
            if (date.isAfter(now) || date.year > year || (month != null && date.month.value > month)) {
                break
            }
        }
    }

    private fun replayDaily(date: LocalDate) {
        computeDailyReads(date)
    }

    private fun replayMonthly(date: LocalDate) {
        computeMonthlyReads(date)
    }

    private fun getDailyKpiOutputPath(date: LocalDate, filename: String): String =
        "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/$filename"

    private fun getMonthlyKpiOutputPath(date: LocalDate, filename: String): String =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/$filename"

    private fun createDailyInputStreamIterator(date: LocalDate, dao: Repository<*>): StorageInputStreamIterator {
        val urls = mutableListOf<URL>()
        urls.addAll(dao.getURLs(date.minusDays(1)))
        urls.addAll(dao.getURLs(date))

        return StorageInputStreamIterator(urls, storage)
    }

    private fun createMonthlyInputStreamIterator(date: LocalDate, dao: Repository<*>): StorageInputStreamIterator {
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
