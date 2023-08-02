package com.wutsi.tracking.manager.service

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.Repository
import com.wutsi.tracking.manager.dao.DailyFromRepository
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.dao.DailyReaderRepository
import com.wutsi.tracking.manager.dao.MonthlyFromRepository
import com.wutsi.tracking.manager.dao.MonthlyReadRepository
import com.wutsi.tracking.manager.dao.MonthlyReaderRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.StorageInputStreamIterator
import com.wutsi.tracking.manager.service.aggregator.from.DailyFromMapper
import com.wutsi.tracking.manager.service.aggregator.from.FromOutputWriter
import com.wutsi.tracking.manager.service.aggregator.from.FromReducer
import com.wutsi.tracking.manager.service.aggregator.from.MonthlyFromMapper
import com.wutsi.tracking.manager.service.aggregator.from.YearlyFromMapper
import com.wutsi.tracking.manager.service.aggregator.reader.DailyReaderMapper
import com.wutsi.tracking.manager.service.aggregator.reader.MonthlyReaderMapper
import com.wutsi.tracking.manager.service.aggregator.reader.ReaderOutputWriter
import com.wutsi.tracking.manager.service.aggregator.reader.ReaderReducer
import com.wutsi.tracking.manager.service.aggregator.reader.YearlyReaderMapper
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.MonthlyReadMapper
import com.wutsi.tracking.manager.service.aggregator.reads.ReadOutputWriter
import com.wutsi.tracking.manager.service.aggregator.reads.ReadReducer
import com.wutsi.tracking.manager.service.aggregator.reads.YearlyReadMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class KpiService(
    private val storage: StorageService,
    private val trackDao: TrackRepository,

    private val dailyReadDao: DailyReadRepository,
    private val monthlyReadDao: MonthlyReadRepository,

    private val dailyReaderDao: DailyReaderRepository,
    private val monthlyReaderDao: MonthlyReaderRepository,

    private val dailyFromDao: DailyFromRepository,
    private val monthlyFromDao: MonthlyFromRepository,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiService::class.java)
    }

    fun replay(year: Int, month: Int?) {
        val now = LocalDate.now(ZoneId.of("UTC"))

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
        computeDailyReaders(date)
        computeDailyFrom(date)
    }

    fun computeMonthly(date: LocalDate) {
        computeMonthlyReads(date)
        computeMonthlyReaders(date)
        computeMonthlyFrom(date)
    }

    fun computeYearly(date: LocalDate) {
        computeYearlyReads(date)
        computeYearlyReaders(date)
        computeYearlyFrom(date)
    }

    private fun computeDailyReads(date: LocalDate) {
        LOGGER.info("$date - Generating Daily Reads")
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
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly Reads")
        Aggregator(
            dao = dailyReadDao,
            inputs = createMonthlyInputStreamIterator(date, dailyReadDao),
            mapper = MonthlyReadMapper(),
            reducer = ReadReducer(),
            output = ReadOutputWriter(getMonthlyKpiOutputPath(date, monthlyReadDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyReads(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly Reads")
        Aggregator(
            dao = monthlyReadDao,
            inputs = createYearlyInputStreamIterator(date, monthlyReadDao),
            mapper = YearlyReadMapper(),
            reducer = ReadReducer(),
            output = ReadOutputWriter(getYearlyKpiOutputPath(date, monthlyReadDao.filename()), storage),
        ).aggregate()
    }

    private fun computeDailyReaders(date: LocalDate) {
        LOGGER.info("$date - Generating Daily Readers")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyReaderMapper(),
            reducer = ReaderReducer(),
            output = ReaderOutputWriter(getDailyKpiOutputPath(date, dailyReaderDao.filename()), storage),
            filter = DailyReadFilter(date),
        ).aggregate()
    }

    private fun computeMonthlyReaders(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly Readers")
        Aggregator(
            dao = dailyReaderDao,
            inputs = createMonthlyInputStreamIterator(date, dailyReaderDao),
            mapper = MonthlyReaderMapper(),
            reducer = ReaderReducer(),
            output = ReaderOutputWriter(getMonthlyKpiOutputPath(date, monthlyReaderDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyReaders(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly Readers")
        Aggregator(
            dao = monthlyReaderDao,
            inputs = createYearlyInputStreamIterator(date, monthlyReaderDao),
            mapper = YearlyReaderMapper(),
            reducer = ReaderReducer(),
            output = ReaderOutputWriter(getYearlyKpiOutputPath(date, monthlyReaderDao.filename()), storage),
        ).aggregate()
    }

    private fun computeDailyFrom(date: LocalDate) {
        LOGGER.info("$date - Generating Daily From")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyFromMapper(),
            reducer = FromReducer(),
            output = FromOutputWriter(getDailyKpiOutputPath(date, dailyFromDao.filename()), storage),
            filter = DailyReadFilter(date),
        ).aggregate()
    }

    private fun computeMonthlyFrom(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly From")
        Aggregator(
            dao = dailyFromDao,
            inputs = createMonthlyInputStreamIterator(date, dailyFromDao),
            mapper = MonthlyFromMapper(),
            reducer = FromReducer(),
            output = FromOutputWriter(getMonthlyKpiOutputPath(date, monthlyFromDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyFrom(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly From")
        Aggregator(
            dao = monthlyFromDao,
            inputs = createYearlyInputStreamIterator(date, monthlyFromDao),
            mapper = YearlyFromMapper(),
            reducer = FromReducer(),
            output = FromOutputWriter(getYearlyKpiOutputPath(date, monthlyFromDao.filename()), storage),
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
        urls.addAll(dao.getURLs(date.plusDays(1)))

        return StorageInputStreamIterator(urls, storage)
    }

    private fun createMonthlyInputStreamIterator(date: LocalDate, dao: Repository<*>): StorageInputStreamIterator {
        val urls = mutableListOf<URL>()
        var cur = LocalDate.of(date.year, date.month, 1)
        val today = LocalDate.now(ZoneId.of("UTC"))
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
