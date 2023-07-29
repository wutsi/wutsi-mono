package com.wutsi.blog.story.service

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.blog.user.service.UserService
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.file.Files
import javax.transaction.Transactional

@Service
class StoryFeedsService(
    private val storyService: StoryService,
    private val topicService: TopicService,
    private val userService: UserService,
    private val mapper: StoryMapper,
    private val storage: StorageService,
    private val logger: KVLogger,

    @Value("\${wutsi.website.url}") private val websiteUrl: String,
) {
    companion object {
        private val HEADERS = arrayOf(
            "id",
            "title",
            "author_id",
            "author",
            "language",
            "topic_id",
            "topic",
            "parent_topic_id",
            "parent_topic",
            "tags",
            "url",
            "summary",
        )
    }

    @Transactional
    fun generate(): Long {
        val stories = findStories()
        logger.add("story_count", stories.size)
        val file = Files.createTempFile("stories", ".csv").toFile()
        toCsv(stories, file)

        val input = FileInputStream(file)
        input.use {
            val url = storage.store("feeds/stories.csv", input, "text/csv", null, "utf-8")
            logger.add("feed_url", url)
        }

        return stories.size.toLong()
    }

    private fun toCsv(stories: List<StoryEntity>, file: File) {
        val authorIds = stories.map { it.userId }.toSet()
        val authorMap = userService.search(
            SearchUserRequest(
                userIds = authorIds.toList(),
                limit = authorIds.size,
            ),
        ).associateBy { it.id }

        val topicMap = topicService.all().associateBy { it.id }

        val fout = FileOutputStream(file)
        fout.use {
            val writer = BufferedWriter(OutputStreamWriter(fout))
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader(*HEADERS)
                    .build(),
            )
            printer.use {
                stories.forEach { story ->
                    val topic = topicMap[story.topicId]
                    val parentTopic = topic?.parentId?.let { topicMap[it] }
                    val author = authorMap[story.userId]

                    printer.printRecord(
                        story.id,
                        story.title,
                        author?.id,
                        author?.fullName,
                        story.language,
                        topic?.id,
                        topic?.name,
                        parentTopic?.id,
                        parentTopic?.name,
                        story.tags.map { it.displayName }.joinToString("|"),
                        websiteUrl + mapper.slug(story, null),
                        story.summary,
                    )
                }
            }
        }
    }

    private fun findStories(): List<StoryEntity> {
        val result = mutableListOf<StoryEntity>()
        var offset = 0
        val limit = 100
        while (true) {
            val stories = storyService.searchStories(
                SearchStoryRequest(
                    status = StoryStatus.PUBLISHED,
                    offset = offset,
                    limit = limit,
                ),
            )
            result.addAll(stories)

            if (stories.size < limit) {
                break
            }
            offset += stories.size
        }
        return result
    }
}
