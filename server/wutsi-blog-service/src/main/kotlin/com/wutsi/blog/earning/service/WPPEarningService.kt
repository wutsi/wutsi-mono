package com.wutsi.blog.earning.service

import com.wutsi.blog.earning.entity.CSVAware
import com.wutsi.blog.earning.entity.WPPStoryEntity
import com.wutsi.blog.earning.entity.WPPUserEntity
import com.wutsi.blog.kpi.domain.StoryKpiEntity
import com.wutsi.blog.kpi.dto.Dimension
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.service.KpiService
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max

@Service
class WPPEarningService(
    private val storyService: StoryService,
    private val kpiService: KpiService,
    private val storageService: StorageService,
    private val logger: KVLogger,
) {
    fun compile(year: Int, month: Int, budget: Long) {
        logger.add("year", year)
        logger.add("month", month)
        logger.add("budget", budget)

        val stories = loadStories(year, month)
        if (stories.isEmpty()) {
            return
        }

        val wstories = compileStories(year, month, budget, stories)
        logger.add("story_count", wstories.size)

        val wusers = compileUsers(year, month, wstories)
        logger.add("user_count", wusers.size)
    }

    private fun compileStories(year: Int, month: Int, budget: Long, stories: List<StoryEntity>): List<WPPStoryEntity> {
        val wstories = toWPPStories(year, month, stories)
        computeStoriesEarnings(wstories, budget)
        computeStoryBonus(wstories, budget)
        val file = toCSV(WPPStoryEntity.csvHeader(), wstories)
        store(year, month, file, "wpp-story.csv")
        return wstories
    }

    private fun compileUsers(year: Int, month: Int, wstories: List<WPPStoryEntity>): List<WPPUserEntity> {
        val wusers = toWPPUsers(wstories)
        val file = toCSV(WPPUserEntity.csvHeader(), wusers)
        store(year, month, file, "wpp-user.csv")
        return wusers
    }

    private fun toCSV(headers: Array<String>, items: List<CSVAware>): File {
        val file = File.createTempFile("earning", ".csv")
        val fout = FileOutputStream(file)
        fout.use {
            val writer = BufferedWriter(OutputStreamWriter(fout))
            writer.use {
                val printer = CSVPrinter(
                    writer,
                    CSVFormat.DEFAULT
                        .builder()
                        .setHeader(*headers)
                        .build(),
                )
                printer.use {
                    items.forEach { it.printCSV(printer) }
                    printer.flush()
                }
            }
        }
        return file
    }

    private fun store(year: Int, month: Int, file: File, name: String) {
        val fin = FileInputStream(file)
        val date = LocalDate.of(year, month, 1)
        fin.use {
            val path = "earnings/" +
                date.format(DateTimeFormatter.ofPattern("yyyy/MM")) +
                "/$name"
            storageService.store(path, fin, "text/csv")
        }
    }

    private fun computeStoriesEarnings(wstories: List<WPPStoryEntity>, budget: Long) {
        val totalReadTime = wstories.sumOf { it.readTime }
        wstories.forEach { story ->
            story.readRatio = if (story.readCount == 0L) 0.0 else story.readerCount.toDouble() / story.readCount
            story.earningAdjustment = story.readRatio * story.wppScore
            story.earningRatio = story.readTime.toDouble() / totalReadTime
            story.earnings = toMoney(story.earningRatio * story.earningAdjustment * budget)
        }
    }

    private fun computeStoryBonus(wstories: List<WPPStoryEntity>, budget: Long) {
        val bonus = max(0, budget - wstories.sumOf { it.earnings })
        val totalEngagement = wstories.sumOf { it.engagementCount }
        wstories.forEach { story ->
            story.engagementRatio = story.engagementCount.toDouble() / totalEngagement
            story.bonus = toMoney(story.engagementRatio * bonus)
        }
    }

    private fun toMoney(amount: Double): Long =
        (amount.toLong() / 10) * 10

    private fun toWPPStories(year: Int, month: Int, stories: List<StoryEntity>): List<WPPStoryEntity> {
        val kpis = loadKpis(year, month, stories)
        val readKpis = kpis.filter { it.type == KpiType.READ }.associateBy { it.storyId }
        val readerKpis = kpis.filter { it.type == KpiType.READER }.associateBy { it.storyId }
        val durationKpis = kpis.filter { it.type == KpiType.DURATION }.associateBy { it.storyId }
        val likeKpis = kpis.filter { it.type == KpiType.LIKE }.associateBy { it.storyId }
        val commentKpis = kpis.filter { it.type == KpiType.COMMENT }.associateBy { it.storyId }
        val clickKpis = kpis.filter { it.type == KpiType.CLICK }.associateBy { it.storyId }

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
                likeCount = likeKpis[story.id]?.value ?: 0L,
                commentCount = commentKpis[story.id]?.value ?: 0L,
                clickCount = clickKpis[story.id]?.value ?: 0L,
            )
        }
    }

    private fun toWPPUsers(wstories: List<WPPStoryEntity>): List<WPPUserEntity> =
        wstories.groupBy { it.userId }
            .map { entry ->
                WPPUserEntity(
                    userId = entry.key,
                    earnings = entry.value.sumOf { it.earnings },
                    bonus = entry.value.sumOf { it.bonus },
                )
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
                    KpiType.LIKE,
                    KpiType.COMMENT,
                    KpiType.CLICK,
                ),
                fromDate = fromDate,
                toDate = toDate,
                dimension = Dimension.ALL,
                storyIds = stories.mapNotNull { it.id }
            )
        )
    }
}
