package com.wutsi.blog.earning.service

import com.wutsi.blog.earning.entity.WPPStoryEntity
import com.wutsi.blog.kpi.domain.StoryKpiEntity
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.service.KpiService
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.LocalDate

@Service
class WPPEarningService(
    private val storyService: StoryService,
    private val kpiService: KpiService,
    private val storageService: TrackingStorageService
) {
    fun compile(year: Int, month: Int, totalAmount: Long) {
        val stories = loadStories(year, month)
        if (stories.isEmpty()) {
            return
        }

        val wstories = toWPPStories(year, month, stories)
        computeEarnings(wstories)
        computeBonus(wstories)

        val file = File.createTempFile("earning-$year-$month", ".csv")
        toCSV(wstories, file)
        store(year, month, file)
    }

    private fun store(year: Int, month: Int, file: File) {
        val fin = FileInputStream(file)
        fin.use {
            val path = "earnings/$year/$month/wpp.csv"
            storageService.store(path, fin, "text/csv")
        }
    }

    private fun toCSV(wstories: List<WPPStoryEntity>, file: File) {
        val fout = FileOutputStream(file)
        fout.use {
            toCSV(wstories, fout)
        }
    }

    private fun toCSV(wstories: List<WPPStoryEntity>, out: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(out))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader(
                        "story_id",
                        "user_id",
                        "read_count",
                        "reader_count",
                        "subscription_count",
                        "read_time",
                        "earning_ratio",
                        "earning_adjustment",
                        "earnings",
                        "bonus_ratio",
                        "bonus_ratio"
                    )
                    .build(),
            )
            printer.use {
                wstories.forEach { story ->
                    printer.printRecord(
                        story.id,
                        story.userId,
                        story.readCount,
                        story.readerCount,
                        story.subscriptionCount,
                        story.readTime,
                        story.earningRatio,
                        story.earningAdjustment,
                        story.earnings,
                        story.bonusRatio,
                        story.bonus,
                    )
                }
                printer.flush()
            }
        }
    }

    private fun computeEarnings(wstories: List<WPPStoryEntity>) {
        val totalReadTime = wstories.sumOf { it.readTime }
        wstories.forEach { story ->
            story.readRatio = if (story.readCount == 0L) 0.0 else story.readerCount.toDouble() / story.readCount
            story.earningAdjustment = story.readRatio * story.wppScore
            story.earningRatio = story.readTime.toDouble() / totalReadTime
            story.earnings = toMoney(story.earningRatio * story.earningAdjustment * totalReadTime)
        }
    }

    private fun computeBonus(wstories: List<WPPStoryEntity>) {
        val bonus = wstories.sumOf { it.earnings }
        val totalSubscriptions = wstories.sumOf { it.subscriptionCount }
        wstories.forEach { story ->
            story.bonusRatio = story.subscriptionCount.toDouble() / totalSubscriptions
            story.bonus = toMoney(story.bonusRatio * bonus)
        }
    }

    private fun toMoney(amount: Double): Long =
        (amount.toLong() / 10) * 10

    private fun toWPPStories(year: Int, month: Int, stories: List<StoryEntity>): List<WPPStoryEntity> {
        val kpis = loadKpis(year, month, stories)
        val readKpis = kpis.filter { it.type == KpiType.READ }.associateBy { it.storyId }
        val readerKpis = kpis.filter { it.type == KpiType.READER }.associateBy { it.storyId }
        val durationKpis = kpis.filter { it.type == KpiType.DURATION }.associateBy { it.storyId }
        val subscriptionKpis = kpis.filter { it.type == KpiType.SUBSCRIPTION }.associateBy { it.storyId }

        return stories.map { story ->
            val readCount = readKpis[story.id]?.value ?: 0L
            val readerCount = readerKpis[story.id]?.value ?: 0L
            WPPStoryEntity(
                id = story.id!!,
                userId = story.userId,
                wppScore = story.wppScore.toDouble() / 100.0,
                readCount = readCount,
                readerCount = readerCount,
                readTime = durationKpis[story.id]?.value ?: 0L,
                subscriptionCount = subscriptionKpis[story.id]?.value ?: 0L,
            )
        }
    }

    private fun loadStories(year: Int, month: Int): List<StoryEntity> {
        val fromDate = LocalDate.of(year, month, 1)
        val toDate = fromDate.plusMonths(1).minusDays(1)
        val storyIds = kpiService.search(
            SearchStoryKpiRequest(
                types = listOf(
                    KpiType.READ,
                ),
                fromDate = fromDate,
                toDate = toDate,
                dimension = Dimension.ALL,
            )
        ).map { it.storyId }
        return storyService.searchStories(
            SearchStoryRequest(
                storyIds = storyIds,
                wpp = true,
                limit = storyIds.size
            )
        )
    }

    private fun loadKpis(year: Int, month: Int, stories: List<StoryEntity>): List<StoryKpiEntity> {
        val fromDate = LocalDate.of(year, month, 1)
        val toDate = fromDate.plusMonths(1).minusDays(1)
        return kpiService.search(
            SearchStoryKpiRequest(
                types = listOf(
                    KpiType.READ,
                    KpiType.READER,
                    KpiType.DURATION,
                    KpiType.SUBSCRIPTION
                ),
                fromDate = fromDate,
                toDate = toDate,
                dimension = Dimension.ALL,
                storyIds = stories.mapNotNull { it.id }
            )
        )
    }
}
