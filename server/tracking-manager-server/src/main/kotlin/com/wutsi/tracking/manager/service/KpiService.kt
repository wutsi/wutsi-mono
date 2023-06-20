package com.wutsi.tracking.manager.service

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.Repository
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.dao.DailyScrollRepository
import com.wutsi.tracking.manager.dao.MonthlyReadRepository
import com.wutsi.tracking.manager.dao.MonthlyScrollRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.StorageInputStreamIterator
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.MonthlyReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.ReadOutputWriter
import com.wutsi.tracking.manager.service.aggregator.reads.ReadReducer
import com.wutsi.tracking.manager.service.aggregator.reads.YearlyReadMapper
import com.wutsi.tracking.manager.service.aggregator.scrolls.AvgScrollReducer
import com.wutsi.tracking.manager.service.aggregator.scrolls.DailyScrollFilter
import com.wutsi.tracking.manager.service.aggregator.scrolls.DailyScrollMapper
import com.wutsi.tracking.manager.service.aggregator.scrolls.MaxScrollReducer
import com.wutsi.tracking.manager.service.aggregator.scrolls.MonthlyScrollMapper
import com.wutsi.tracking.manager.service.aggregator.scrolls.ScrollOutputWriter
import com.wutsi.tracking.manager.service.aggregator.scrolls.YearlyScrollMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class KpiService(
    private val storage: StorageService,
    private val trackDao: TrackRepository,
    private val dailyReadDao: DailyReadRepository,
    private val monthlyReadDao: MonthlyReadRepository,
    private val dailyScrollDao: DailyScrollRepository,
    private val monthlyScrollDao: MonthlyScrollRepository,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiService::class.java)
    }

    fun replay(year: Int, month: Int?) {
        val now = LocalDate.now()

        // Daily KPIs
        var date = LocalDate.of(year, month ?: 1, 1)
        while (true) {
            computeDaily(date)

            date = date.plusDays(1)
            if (date.isAfter(now) || date.year > year || (month != null && date.month.value > month)) {
                break
            }
        }

        // Monthly KPIs
        date = LocalDate.of(year, month ?: 1, 1)
        while (true) {
            computeMonthly(date)

            date = date.plusMonths(1)
            if (date.isAfter(now) || date.year > year || (month != null && date.month.value > month)) {
                break
            }
        }

        // Yearly KPI
        date = LocalDate.of(year, month ?: 1, 1)
        computeYearly(date)
    }

    fun computeDaily(date: LocalDate) {
        computeDailyReads(date)
        computeDailyScrolls(date)
    }

    fun computeMonthly(date: LocalDate) {
        computeMonthlyReads(date)
        computeMonthlyScrolls(date)
    }

    fun computeYearly(date: LocalDate) {
        computeYearlyReads(date)
        computeYearlyScrolls(date)
    }

    private fun computeDailyReads(date: LocalDate) {
        LOGGER.info("Generating Daily Reads")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyReadMapper(),
            reducer = ReadReducer(),
            output = ReadOutputWriter(getDailyKpiOutputPath(date, dailyReadDao.filename()), storage),
            filter = DailyReadFilter(date),
        ).aggregate()
    }

    private fun computeMonthlyReads(date: LocalDate) {
        LOGGER.info("Generating Monthly Reads")
        Aggregator(
            dao = dailyReadDao,
            inputs = createMonthlyInputStreamIterator(date, dailyReadDao),
            mapper = MonthlyReadMapper(),
            reducer = ReadReducer(),
            output = ReadOutputWriter(getMonthlyKpiOutputPath(date, monthlyReadDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyReads(date: LocalDate) {
        LOGGER.info("Generating Yearly Reads")
        Aggregator(
            dao = monthlyReadDao,
            inputs = createYearlyInputStreamIterator(date, monthlyReadDao),
            mapper = YearlyReadMapper(),
            reducer = ReadReducer(),
            output = ReadOutputWriter(getYearlyKpiOutputPath(date, monthlyReadDao.filename()), storage),
        ).aggregate()
    }

    private fun computeDailyScrolls(date: LocalDate) {
        LOGGER.info("Generating Daily Scrolls")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyScrollMapper(),
            reducer = MaxScrollReducer(),
            output = ScrollOutputWriter(getDailyKpiOutputPath(date, dailyScrollDao.filename()), storage),
            filter = DailyScrollFilter(date),
        ).aggregate()
    }

    private fun computeMonthlyScrolls(date: LocalDate) {
        LOGGER.info("Generating Monthly Scrolls")
        Aggregator(
            dao = dailyScrollDao,
            inputs = createMonthlyInputStreamIterator(date, dailyScrollDao),
            mapper = MonthlyScrollMapper(),
            reducer = AvgScrollReducer(),
            output = ScrollOutputWriter(getMonthlyKpiOutputPath(date, dailyScrollDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyScrolls(date: LocalDate) {
        LOGGER.info("Generating Yearly Scrolls")
        Aggregator(
            dao = monthlyScrollDao,
            inputs = createYearlyInputStreamIterator(date, monthlyScrollDao),
            mapper = YearlyScrollMapper(),
            reducer = AvgScrollReducer(),
            output = ScrollOutputWriter(getYearlyKpiOutputPath(date, monthlyScrollDao.filename()), storage),
        ).aggregate()
    }

    private fun getDailyKpiOutputPath(date: LocalDate, filename: String): String =
        "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/$filename"

    private fun getMonthlyKpiOutputPath(date: LocalDate, filename: String): String =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/$filename"

    private fun getYearlyKpiOutputPath(date: LocalDate, filename: String): String =
        "kpi/yearly/" + date.format(DateTimeFormatter.ofPattern("yyyy")) + "/$filename"

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

    private fun createYearlyInputStreamIterator(date: LocalDate, dao: Repository<*>): StorageInputStreamIterator {
        val urls = mutableListOf<URL>()
        var cur = LocalDate.of(date.year, 1, 1)
        while (true) {
            urls.addAll(dao.getURLs(cur))

            cur = cur.plusMonths(1)
            if (cur.year != date.year) {
                break
            }
        }
        return StorageInputStreamIterator(urls, storage)
    }
}
