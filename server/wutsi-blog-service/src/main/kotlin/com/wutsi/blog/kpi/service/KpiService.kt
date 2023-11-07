package com.wutsi.blog.kpi.service

import com.wutsi.blog.kpi.dao.SearchStoryKpiMonthlyQueryBuilder
import com.wutsi.blog.kpi.dao.SearchUserKpiMonthlyQueryBuilder
import com.wutsi.blog.kpi.domain.StoryKpiEntity
import com.wutsi.blog.kpi.domain.UserKpiEntity
import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.SearchStoryKpiRequest
import com.wutsi.blog.kpi.dto.SearchUserKpiRequest
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.logging.KVLogger
import jakarta.persistence.EntityManager
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class KpiService(
    private val storage: TrackingStorageService,
    private val persister: KpiPersister,
    private val storyService: StoryService,
    private val userService: UserService,
    private val logger: KVLogger,
    private val em: EntityManager,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiService::class.java)
    }

    fun replay(year: Int, month: Int? = null) {
        val now = LocalDate.now(ZoneId.of("UTC"))
        logger.add("command", "ReplayKpiCommand")

        var date = LocalDate.of(year, month ?: 1, 1)
        while (true) {
            import(date)

            date = date.plusMonths(1)
            if (date.isAfter(now) || date.year > year || (month != null && date.month.value > month)) {
                break
            }
        }
    }

    fun import(date: LocalDate): Long =
        importSubscriptions(date) +
            importReadsBySource(date) +
            importDuration(date) +
            importClick(date) +

            // IMPORTANT: MUST BE THE LAST TO IMPORT
            importReads(date)

    private fun importSubscriptions(date: LocalDate): Long =
        persister.persistPersister(date).toLong()

    private fun importReadsBySource(date: LocalDate): Long {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/source.csv"
        val result = try {
            val file = downloadTrackingFile(path)
            try {
                importStoryReadsBySource(date, file)
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn(">>> Unable to log KPIs for $date from $path")
            0L
        }

        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Importing Monthly Reads from $path - $result imported")
        return result
    }

    private fun importDuration(date: LocalDate): Long {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/durations.csv"
        val result = try {
            val file = downloadTrackingFile(path)
            try {
                importStoryDuration(date, file)
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn(">>> Unable to log KPIs for $date from $path")
            0L
        }

        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Importing Monthly Duration from $path - $result imported")
        return result
    }

    private fun importReads(date: LocalDate): Long {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv"
        val storyIds = mutableSetOf<Long>()
        val userIds = mutableSetOf<Long>()
        val result = try {
            val file = downloadTrackingFile(path)
            try {
                val result = importStoryReads(date, file, storyIds)
                updateStoryKpis(storyIds, userIds)

                importUserKpi(date, KpiType.READ, userIds.toList())
                importUserKpi(date, KpiType.DURATION, userIds.toList())
                importUserKpi(date, KpiType.CLICK, userIds.toList())
                updateUserKpis(userIds)

                result
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn(">>> Unable to log KPIs for $date from $path")
            0L
        }

        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Importing Monthly Reads from $path - $result imported")
        return result
    }

    private fun importClick(date: LocalDate): Long {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/clicks.csv"
        val result = try {
            val file = downloadTrackingFile(path)
            try {
                importStoryClick(date, file)
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn(">>> Unable to log KPIs for $date from $path")
            0L
        }

        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Importing Monthly Clicks from $path - $result imported")
        return result
    }

    fun search(request: SearchStoryKpiRequest): List<StoryKpiEntity> {
        val builder = SearchStoryKpiMonthlyQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, StoryKpiEntity::class.java)
        Predicates.setParameters(query, params)

        return query.resultList as List<StoryKpiEntity>
    }

    fun search(request: SearchUserKpiRequest): List<UserKpiEntity> {
        val builder = SearchUserKpiMonthlyQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, UserKpiEntity::class.java)
        Predicates.setParameters(query, params)

        return query.resultList as List<UserKpiEntity>
    }

    private fun importStoryReads(
        date: LocalDate,
        file: File,
        storyIds: MutableSet<Long>,
    ): Long {
        var result = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("product_id", "total_reads")
                .build(),
        )
        parser.use {
            for (record in parser) {
                var storyId = -1L
                var value = -1L
                try {
                    storyId = record.get(0)?.trim()?.toLong() ?: 0
                    value = record.get(1)?.trim()?.toLong() ?: 0
                    persister.persistStory(date, KpiType.READ, storyId, value)
                    storyIds.add(storyId)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to store StoryKPI - type=READ, story-id=$storyId, value=$value", ex)
                }
            }
            return result
        }
    }

    private fun importStoryReadsBySource(
        date: LocalDate,
        file: File,
    ): Long {
        var result = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("product_id", "source", "total_reads")
                .build(),
        )
        parser.use {
            for (record in parser) {
                var storyId = -1L
                var value = -1L
                var source = TrafficSource.UNKNOWN
                try {
                    storyId = record.get(0)?.trim()?.toLong() ?: 0
                    value = record.get(2)?.trim()?.toLong() ?: 0
                    source = try {
                        TrafficSource.valueOf(record.get(1)!!.trim())
                    } catch (e: Exception) {
                        TrafficSource.UNKNOWN
                    }

                    persister.persistStory(date, KpiType.READ, storyId, value, source = source)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to store KPI - type=READ, source=$source, story-id=$storyId, value=$value", ex)
                }
            }
            return result
        }
    }

    private fun importStoryDuration(
        date: LocalDate,
        file: File,
    ): Long {
        var result = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("correlation_id", "product_id", "total_seconds")
                .build(),
        )
        parser.use {
            for (record in parser) {
                var storyId = -1L
                var value = -1L
                try {
                    storyId = record.get(1)?.trim()?.toLong() ?: 0
                    value = record.get(2)?.trim()?.toLong() ?: 0

                    persister.persistStory(date, KpiType.DURATION, storyId, value)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to store KPI - type=DURATION, story-id=$storyId, value=$value", ex)
                }
            }
            return result
        }
    }

    private fun importStoryClick(
        date: LocalDate,
        file: File,
    ): Long {
        var result = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("product_id", "total_clicks")
                .build(),
        )
        parser.use {
            for (record in parser) {
                var storyId = -1L
                var value = -1L
                try {
                    storyId = record.get(0)?.trim()?.toLong() ?: 0
                    value = record.get(1)?.trim()?.toLong() ?: 0
                    persister.persistStory(date, KpiType.CLICK, storyId, value)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to store StoryKPI - type=CLICK, story-id=$storyId, value=$value", ex)
                }
            }
            return result
        }
    }

    private fun importUserKpi(
        date: LocalDate,
        type: KpiType,
        userIds: List<Long>,
    ) {
        userIds.forEach { userId ->
            TrafficSource.values().forEach { source ->
                try {
                    persister.persistUser(date, type, userId, source)
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to store UserKPI - type=$type, user-id=$userId, source=$source", ex)
                }
            }
        }
    }

    private fun updateStoryKpis(storyIds: Set<Long>, userIds: MutableSet<Long>) {
        storyService.searchStories(
            request = SearchStoryRequest(
                storyIds = storyIds.toList(),
                limit = storyIds.size,
            ),
        ).forEach { story ->
            try {
                storyService.onKpisImported(story)
                userIds.add(story.userId)
            } catch (ex: Exception) {
                logger.setException(ex)
            }
        }
    }

    private fun updateUserKpis(userIds: Set<Long>) {
        userService.search(
            request = SearchUserRequest(
                userIds = userIds.toList(),
                limit = userIds.size,
            ),
        ).forEach { user ->
            try {
//                LOGGER.info(">>> Updating KPIs of User#${user.id}")
                userService.onKpisImported(user)
            } catch (ex: Exception) {
                logger.setException(ex)
            }
        }
    }

    private fun downloadTrackingFile(path: String): File {
        val file = File.createTempFile(UUID.randomUUID().toString(), "csv")
        val out = FileOutputStream(file)
        out.use {
            storage.get(path, out)
        }
        return file
    }
}
