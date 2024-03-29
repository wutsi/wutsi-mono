package com.wutsi.tracking.manager.service

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.tracking.manager.Repository
import com.wutsi.tracking.manager.dao.DailyCampaignClickRepository
import com.wutsi.tracking.manager.dao.DailyCampaignRepository
import com.wutsi.tracking.manager.dao.DailyClickRepository
import com.wutsi.tracking.manager.dao.DailyDurationRepository
import com.wutsi.tracking.manager.dao.DailyEmailRepository
import com.wutsi.tracking.manager.dao.DailyReadRepository
import com.wutsi.tracking.manager.dao.DailyReaderRepository
import com.wutsi.tracking.manager.dao.DailySourceRepository
import com.wutsi.tracking.manager.dao.DailyViewRepository
import com.wutsi.tracking.manager.dao.MonthlyCampaignClickRepository
import com.wutsi.tracking.manager.dao.MonthlyCampaignRepository
import com.wutsi.tracking.manager.dao.MonthlyClickRepository
import com.wutsi.tracking.manager.dao.MonthlyDurationRepository
import com.wutsi.tracking.manager.dao.MonthlyEmailRepository
import com.wutsi.tracking.manager.dao.MonthlyReadRepository
import com.wutsi.tracking.manager.dao.MonthlyReaderRepository
import com.wutsi.tracking.manager.dao.MonthlySourceRepository
import com.wutsi.tracking.manager.dao.MonthlyViewRepository
import com.wutsi.tracking.manager.dao.TrackRepository
import com.wutsi.tracking.manager.service.aggregator.Aggregator
import com.wutsi.tracking.manager.service.aggregator.StorageInputStreamIterator
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector
import com.wutsi.tracking.manager.service.aggregator.campaign.CampaignClickOutputWriter
import com.wutsi.tracking.manager.service.aggregator.campaign.CampaignClickReducer
import com.wutsi.tracking.manager.service.aggregator.campaign.CampaignOutputWriter
import com.wutsi.tracking.manager.service.aggregator.campaign.CampaignReducer
import com.wutsi.tracking.manager.service.aggregator.campaign.DailyCampaignClickFilter
import com.wutsi.tracking.manager.service.aggregator.campaign.DailyCampaignClickMapper
import com.wutsi.tracking.manager.service.aggregator.campaign.DailyCampaignFilter
import com.wutsi.tracking.manager.service.aggregator.campaign.DailyCampaignMapper
import com.wutsi.tracking.manager.service.aggregator.campaign.MonthlyCampaignClickMapper
import com.wutsi.tracking.manager.service.aggregator.campaign.MonthlyCampaignMapper
import com.wutsi.tracking.manager.service.aggregator.campaign.YearlyCampaignClickMapper
import com.wutsi.tracking.manager.service.aggregator.campaign.YearlyCampaignMapper
import com.wutsi.tracking.manager.service.aggregator.click.ClickOutputWriter
import com.wutsi.tracking.manager.service.aggregator.click.ClickReducer
import com.wutsi.tracking.manager.service.aggregator.click.DailyClickFilter
import com.wutsi.tracking.manager.service.aggregator.click.DailyClickMapper
import com.wutsi.tracking.manager.service.aggregator.click.MonthlyClickMapper
import com.wutsi.tracking.manager.service.aggregator.click.YearlyClickMapper
import com.wutsi.tracking.manager.service.aggregator.duration.DailyDurationFilter
import com.wutsi.tracking.manager.service.aggregator.duration.DailyDurationMapper
import com.wutsi.tracking.manager.service.aggregator.duration.DailyDurationReducer
import com.wutsi.tracking.manager.service.aggregator.duration.DurationOutputWriter
import com.wutsi.tracking.manager.service.aggregator.duration.MonthlyDurationMapper
import com.wutsi.tracking.manager.service.aggregator.duration.MonthlyDurationReducer
import com.wutsi.tracking.manager.service.aggregator.duration.YearlyDurationMapper
import com.wutsi.tracking.manager.service.aggregator.email.DailyEmailFilter
import com.wutsi.tracking.manager.service.aggregator.email.DailyEmailMapper
import com.wutsi.tracking.manager.service.aggregator.email.EmailOutputWriter
import com.wutsi.tracking.manager.service.aggregator.email.EmailReducer
import com.wutsi.tracking.manager.service.aggregator.email.MonthlyEmailMapper
import com.wutsi.tracking.manager.service.aggregator.email.YearlyEmailMapper
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
import com.wutsi.tracking.manager.service.aggregator.source.DailySourceMapper
import com.wutsi.tracking.manager.service.aggregator.source.MonthlySourceMapper
import com.wutsi.tracking.manager.service.aggregator.source.SourceOutputWriter
import com.wutsi.tracking.manager.service.aggregator.source.SourceReducer
import com.wutsi.tracking.manager.service.aggregator.source.YearlySourceMapper
import com.wutsi.tracking.manager.service.aggregator.views.DailyViewFilter
import com.wutsi.tracking.manager.service.aggregator.views.DailyViewMapper
import com.wutsi.tracking.manager.service.aggregator.views.MonthlyViewMapper
import com.wutsi.tracking.manager.service.aggregator.views.ViewOutputWriter
import com.wutsi.tracking.manager.service.aggregator.views.ViewReducer
import com.wutsi.tracking.manager.service.aggregator.views.YearlyViewMapper
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
    private val trafficSourceDetector: TrafficSourceDetector,

    private val dailyViewDao: DailyViewRepository,
    private val monthlyViewDao: MonthlyViewRepository,

    private val dailyReadDao: DailyReadRepository,
    private val monthlyReadDao: MonthlyReadRepository,

    private val dailyReaderDao: DailyReaderRepository,
    private val monthlyReaderDao: MonthlyReaderRepository,

    private val dailySourceDao: DailySourceRepository,
    private val monthlySourceDao: MonthlySourceRepository,

    private val dailyEmailDao: DailyEmailRepository,
    private val monthlyEmailDao: MonthlyEmailRepository,

    private val dailyDurationDao: DailyDurationRepository,
    private val monthlyDurationDao: MonthlyDurationRepository,

    private val dailyClickDao: DailyClickRepository,
    private val monthlyClickDao: MonthlyClickRepository,

    private val dailyCampaignDao: DailyCampaignRepository,
    private val monthlyCampaignDao: MonthlyCampaignRepository,

    private val dailyCampaignClickDao: DailyCampaignClickRepository,
    private val monthlyCampaignClickDao: MonthlyCampaignClickRepository,
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
        computeDailySource(date)
        computeDailyEmail(date)
        computeDailyDuration(date)
        computeDailyClicks(date)
        computeDailyViews(date)
        computeDailyCampaigns(date)
        computeDailyCampaignClicks(date)
    }

    fun computeMonthly(date: LocalDate) {
        computeMonthlyReads(date)
        computeMonthlyReaders(date)
        computeMonthlySource(date)
        computeMonthlyEmail(date)
        computeMonthlyDuration(date)
        computeMonthlyClicks(date)
        computeMonthlyViews(date)
        computeMonthlyCampaigns(date)
        computeMonthlyCampaignClicks(date)
    }

    fun computeYearly(date: LocalDate) {
        computeYearlyReads(date)
        computeYearlyReaders(date)
        computeYearlySource(date)
        computeYearlyEmail(date)
        computeYearlyDuration(date)
        computeYearlyClicks(date)
        computeYearlyViews(date)
        computeYearlyCampaigns(date)
        computeYearlyCampaignClicks(date)
    }

    // Views
    private fun computeDailyViews(date: LocalDate) {
        LOGGER.info("$date - Generating Daily Views")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyViewMapper(),
            reducer = ViewReducer(),
            output = ViewOutputWriter(getDailyKpiOutputPath(date, dailyViewDao.filename()), storage),
            filter = DailyViewFilter(date),
        ).aggregate()
    }

    private fun computeMonthlyViews(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly Views")
        Aggregator(
            dao = dailyViewDao,
            inputs = createMonthlyInputStreamIterator(date, dailyViewDao),
            mapper = MonthlyViewMapper(),
            reducer = ViewReducer(),
            output = ViewOutputWriter(getMonthlyKpiOutputPath(date, monthlyViewDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyViews(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly Views")
        Aggregator(
            dao = monthlyViewDao,
            inputs = createYearlyInputStreamIterator(date, monthlyViewDao),
            mapper = YearlyViewMapper(),
            reducer = ViewReducer(),
            output = ViewOutputWriter(getYearlyKpiOutputPath(date, monthlyViewDao.filename()), storage),
        ).aggregate()
    }

    // Raads
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

    // Raaders
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

    // Source
    private fun computeDailySource(date: LocalDate) {
        LOGGER.info("$date - Generating Daily Source")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailySourceMapper(trafficSourceDetector),
            reducer = SourceReducer(),
            output = SourceOutputWriter(getDailyKpiOutputPath(date, dailySourceDao.filename()), storage),
            filter = DailyReadFilter(date),
        ).aggregate()
    }

    private fun computeMonthlySource(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly Source")
        Aggregator(
            dao = dailySourceDao,
            inputs = createMonthlyInputStreamIterator(date, dailySourceDao),
            mapper = MonthlySourceMapper(),
            reducer = SourceReducer(),
            output = SourceOutputWriter(getMonthlyKpiOutputPath(date, monthlySourceDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlySource(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly Source")
        Aggregator(
            dao = monthlySourceDao,
            inputs = createYearlyInputStreamIterator(date, monthlySourceDao),
            mapper = YearlySourceMapper(),
            reducer = SourceReducer(),
            output = SourceOutputWriter(getYearlyKpiOutputPath(date, monthlySourceDao.filename()), storage),
        ).aggregate()
    }

    // Email
    private fun computeDailyEmail(date: LocalDate) {
        LOGGER.info("$date - Generating Daily Email")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyEmailMapper(),
            reducer = EmailReducer(),
            output = EmailOutputWriter(getDailyKpiOutputPath(date, dailyEmailDao.filename()), storage),
            filter = DailyEmailFilter(DailyReadFilter(date), trafficSourceDetector)
        ).aggregate()
    }

    private fun computeMonthlyEmail(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly Email")
        Aggregator(
            dao = dailyEmailDao,
            inputs = createMonthlyInputStreamIterator(date, dailyEmailDao),
            mapper = MonthlyEmailMapper(),
            reducer = EmailReducer(),
            output = EmailOutputWriter(getMonthlyKpiOutputPath(date, monthlyEmailDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyEmail(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly Email")
        Aggregator(
            dao = monthlyEmailDao,
            inputs = createYearlyInputStreamIterator(date, monthlyEmailDao),
            mapper = YearlyEmailMapper(),
            reducer = EmailReducer(),
            output = EmailOutputWriter(getYearlyKpiOutputPath(date, monthlyEmailDao.filename()), storage),
        ).aggregate()
    }

    // Duration
    private fun computeDailyDuration(date: LocalDate) {
        LOGGER.info("$date - Generating Daily Duration")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyDurationMapper(trafficSourceDetector),
            reducer = DailyDurationReducer(),
            output = DurationOutputWriter(getDailyKpiOutputPath(date, dailyDurationDao.filename()), storage),
            filter = DailyDurationFilter(date)
        ).aggregate()
    }

    private fun computeMonthlyDuration(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly Duration")
        Aggregator(
            dao = dailyDurationDao,
            inputs = createMonthlyInputStreamIterator(date, dailyDurationDao),
            mapper = MonthlyDurationMapper(),
            reducer = MonthlyDurationReducer(),
            output = DurationOutputWriter(getMonthlyKpiOutputPath(date, monthlyDurationDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyDuration(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy")) + " - Generating Monthly Duration")
        Aggregator(
            dao = monthlyDurationDao,
            inputs = createYearlyInputStreamIterator(date, monthlyDurationDao),
            mapper = YearlyDurationMapper(),
            reducer = MonthlyDurationReducer(),
            output = DurationOutputWriter(getYearlyKpiOutputPath(date, monthlyDurationDao.filename()), storage),
        ).aggregate()
    }

    // Clicks
    private fun computeDailyClicks(date: LocalDate) {
        LOGGER.info("$date - Generating Daily Clicks")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyClickMapper(),
            reducer = ClickReducer(),
            output = ClickOutputWriter(getDailyKpiOutputPath(date, dailyClickDao.filename()), storage),
            filter = DailyClickFilter(date),
        ).aggregate()
    }

    private fun computeMonthlyClicks(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly Clicks")
        Aggregator(
            dao = dailyClickDao,
            inputs = createMonthlyInputStreamIterator(date, dailyClickDao),
            mapper = MonthlyClickMapper(),
            reducer = ClickReducer(),
            output = ClickOutputWriter(getMonthlyKpiOutputPath(date, monthlyClickDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyClicks(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly Clicks")
        Aggregator(
            dao = monthlyClickDao,
            inputs = createYearlyInputStreamIterator(date, monthlyClickDao),
            mapper = YearlyClickMapper(),
            reducer = ClickReducer(),
            output = ClickOutputWriter(getYearlyKpiOutputPath(date, monthlyClickDao.filename()), storage),
        ).aggregate()
    }

    // Campaigns
    private fun computeDailyCampaigns(date: LocalDate) {
        LOGGER.info("$date - Generating Daily Campaigns")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyCampaignMapper(),
            reducer = CampaignReducer(),
            output = CampaignOutputWriter(getDailyKpiOutputPath(date, dailyCampaignDao.filename()), storage),
            filter = DailyCampaignFilter(date),
        ).aggregate()
    }

    private fun computeMonthlyCampaigns(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly Campaigns")
        Aggregator(
            dao = dailyCampaignDao,
            inputs = createMonthlyInputStreamIterator(date, dailyCampaignDao),
            mapper = MonthlyCampaignMapper(),
            reducer = CampaignReducer(),
            output = CampaignOutputWriter(getMonthlyKpiOutputPath(date, monthlyCampaignDao.filename()), storage),
        ).aggregate()
    }

    private fun computeYearlyCampaigns(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly Campaigns")
        Aggregator(
            dao = monthlyCampaignDao,
            inputs = createYearlyInputStreamIterator(date, monthlyCampaignDao),
            mapper = YearlyCampaignMapper(),
            reducer = CampaignReducer(),
            output = CampaignOutputWriter(getYearlyKpiOutputPath(date, monthlyCampaignDao.filename()), storage),
        ).aggregate()
    }

    // CampaignClicks
    private fun computeDailyCampaignClicks(date: LocalDate) {
        LOGGER.info("$date - Generating Daily CampaignClicks")
        Aggregator(
            dao = trackDao,
            inputs = createDailyInputStreamIterator(date, trackDao),
            mapper = DailyCampaignClickMapper(),
            reducer = CampaignClickReducer(),
            output = CampaignClickOutputWriter(getDailyKpiOutputPath(date, dailyCampaignClickDao.filename()), storage),
            filter = DailyCampaignClickFilter(date),
        ).aggregate()
    }

    private fun computeMonthlyCampaignClicks(date: LocalDate) {
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Generating Monthly CampaignClicks")
        Aggregator(
            dao = dailyCampaignClickDao,
            inputs = createMonthlyInputStreamIterator(date, dailyCampaignClickDao),
            mapper = MonthlyCampaignClickMapper(),
            reducer = CampaignClickReducer(),
            output = CampaignClickOutputWriter(
                getMonthlyKpiOutputPath(date, monthlyCampaignClickDao.filename()),
                storage
            ),
        ).aggregate()
    }

    private fun computeYearlyCampaignClicks(date: LocalDate) {
        LOGGER.info("${date.year} - Generating Yearly CampaignClicks")
        Aggregator(
            dao = monthlyCampaignClickDao,
            inputs = createYearlyInputStreamIterator(date, monthlyCampaignClickDao),
            mapper = YearlyCampaignClickMapper(),
            reducer = CampaignClickReducer(),
            output = CampaignClickOutputWriter(
                getYearlyKpiOutputPath(date, monthlyCampaignClickDao.filename()),
                storage
            ),
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
