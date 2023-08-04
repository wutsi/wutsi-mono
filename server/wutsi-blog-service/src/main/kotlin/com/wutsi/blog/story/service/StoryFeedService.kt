package com.wutsi.blog.story.service

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.StorySortStrategy
import com.wutsi.blog.story.dto.StoryStatus
import com.wutsi.blog.story.mapper.StoryMapper
import com.wutsi.blog.user.domain.UserEntity
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
import java.text.SimpleDateFormat
import javax.transaction.Transactional

@Service
class StoryFeedService(
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
            "published_date",
        )
    }

    @Transactional
    fun generate(): Long {
        val file = Files.createTempFile("stories", ".csv").toFile()
        val result = toCsv(file)

        val input = FileInputStream(file)
        input.use {
            val url = storage.store("feeds/stories.csv", input, "text/csv", null, "utf-8", file.length())
            logger.add("feed_url", url)
        }

        return result
    }

    private fun toCsv(file: File): Long {
        val fout = FileOutputStream(file)
        var result = 0L
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
                var offset = 0
                val limit = 100
                val topicMap = topicService.all().associateBy { it.id }
                val authorMap = mutableMapOf<Long, UserEntity>()
                while (true) {
                    val stories = storyService.searchStories(
                        SearchStoryRequest(
                            status = StoryStatus.PUBLISHED,
                            sortBy = StorySortStrategy.CREATED,
                            offset = offset,
                            limit = limit,
                        ),
                    )
                    if (stories.isEmpty()) {
                        break
                    }

                    loadUsers(authorMap, stories)

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
                            story.publishedDateTime?.let { date -> SimpleDateFormat("yyyy-MM-dd").format(date) },
                        )
                        result++
                    }

                    offset += limit
                }
            }
        }
        return result
    }

    private fun loadUsers(users: MutableMap<Long, UserEntity>, stories: List<StoryEntity>) {
        val userIds = stories.map { it.userId }
            .filter { !users.keys.contains(it) }
        if (userIds.isEmpty()) {
            return
        }

        userService.search(
            SearchUserRequest(
                userIds = userIds.toList(),
                limit = userIds.size,
            ),
        ).forEach { user -> users[user.id!!] = user }
    }
}
