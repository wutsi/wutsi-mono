package com.wutsi.blog.kpi.service

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import com.wutsi.platform.core.logging.DefaultKVLogger
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class KpiService(
    private val storage: TrackingStorageService,
    private val persister: KpiPersister,
    private val storyService: StoryService,
    private val userService: UserService,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiService::class.java)
    }

    fun replay(year: Int, month: Int? = null) {
        val now = LocalDate.now()

        var date = LocalDate.of(year, month ?: 1, 1)
        while (true) {
            importMonthlyReads(date)

            date = date.plusMonths(1)
            if (date.isAfter(now) || date.year > year || (month != null && date.month.value > month)) {
                break
            }
        }
    }

    fun importMonthlyReads(date: LocalDate): Long {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv"
        val storyIds = mutableSetOf<Long>()
        val userIds = mutableSetOf<Long>()
        return try {
            val file = downloadTrackingFile(path)
            try {
                val result = importMonthlyReads(date, file, storyIds)

                updateStoryKpis(date, storyIds, userIds)
                updateUserKpis(date, userIds)
                return result
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to log KPIs for $date from $path", ex)
            0L
        }
    }

    private fun importMonthlyReads(date: LocalDate, file: File, storyIds: MutableSet<Long>): Long =
        importMonthlyKPI(date, file, KpiType.READ, storyIds)

    private fun importMonthlyKPI(
        date: LocalDate,
        file: File,
        type: KpiType,
        storyIds: MutableSet<Long>,
    ): Long {
        var result = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("product_id", "average_scrolls")
                .build(),
        )
        val logger = DefaultKVLogger()
        parser.use {
            for (record in parser) {
                try {
                    val storyId = record.get(0)?.trim()?.toLong() ?: 0
                    val value = record.get(1)?.trim()?.toLong() ?: 0
                    logger.add("date", date)
                    logger.add("story_id", storyId)
                    logger.add("value", value)
                    logger.add("type", type)

                    persister.persist(date, type, storyId, value)
                    storyIds.add(storyId)
                    result++
                } catch (ex: Exception) {
                    logger.setException(ex)
                } finally {
                    logger.log()
                }
            }
            return result
        }
    }

    private fun updateStoryKpis(date: LocalDate, storyIds: Set<Long>, userIds: MutableSet<Long>) {
        val logger = DefaultKVLogger()
        storyService.searchStories(
            request = SearchStoryRequest(
                storyIds = storyIds.toList(),
                limit = storyIds.size,
            ),
        ).forEach { story ->
            try {
                logger.add("date", date)
                logger.add("story_id", story.id)
                logger.add("action", "update-story-kpis")

                storyService.onKpisImported(story)
                userIds.add(story.userId)
            } catch (ex: Exception) {
                logger.setException(ex)
            } finally {
                logger.log()
            }
        }
    }

    private fun updateUserKpis(date: LocalDate, userIds: Set<Long>) {
        val logger = DefaultKVLogger()
        userService.search(
            request = SearchUserRequest(
                userIds = userIds.toList(),
                limit = userIds.size,
            ),
        ).forEach { user ->
            try {
                logger.add("date", date)
                logger.add("user_id", user.id)
                logger.add("action", "update-user-kpis")

                userService.onKpisImported(user)
            } catch (ex: Exception) {
                logger.setException(ex)
            } finally {
                logger.log()
            }
        }
    }

    private fun downloadTrackingFile(path: String): File {
        val file = File.createTempFile(UUID.randomUUID().toString(), "csv")
        val out = FileOutputStream(file)
        out.use {
            LOGGER.info("Downloading $path to $file")
            storage.get(path, out)
        }
        return file
    }
}
