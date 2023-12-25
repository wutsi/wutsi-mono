package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StoryService
import com.wutsi.blog.user.domain.UserEntity
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class CounterKpiUpdater(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val storyService: StoryService,
    private val userService: UserService,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReadKpiImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv"

    override fun import(date: LocalDate, file: File): Long {
        val stories = updateStoryKpis(file)
        updateUserKpis(stories)

        return stories.size.toLong()
    }

    private fun updateStoryKpis(file: File): List<StoryEntity> {
        val storyIds = loadStoryIds(file)
        val stories = storyService.searchStories(
            SearchStoryRequest(
                storyIds = storyIds.toList(),
                limit = storyIds.size
            )
        )
        stories.forEach { story -> storyService.onKpisImported(story) }
        return stories
    }

    private fun updateUserKpis(stories: List<StoryEntity>): List<UserEntity> {
        val userIds = stories.map { it.userId }.toSet()
        val users = userService.search(
            SearchUserRequest(
                userIds = userIds.toList(),
                limit = userIds.size
            )
        )
        users.forEach { user -> userService.onKpisImported(user) }
        return users
    }

    private fun loadStoryIds(file: File): Collection<Long> {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("product_id", "total_reads")
                .build(),
        )

        val storyIds = mutableSetOf<Long>()
        parser.use {
            for (record in parser) {
                val storyId = record.get("product_id")?.ifEmpty { null }?.trim()

                try {
                    storyIds.add(storyId!!.toLong())
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }
        }
        return storyIds
    }
}
