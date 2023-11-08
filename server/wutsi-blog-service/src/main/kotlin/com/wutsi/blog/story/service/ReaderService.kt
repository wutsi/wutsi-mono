package com.wutsi.blog.story.service

import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.ReaderRepository
import com.wutsi.blog.story.dao.SearchReaderQueryBuilder
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.ReaderEntity
import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.domain.ViewEntity
import com.wutsi.blog.story.dto.SearchReaderRequest
import com.wutsi.blog.story.dto.ViewStoryCommand
import com.wutsi.blog.util.Predicates
import com.wutsi.platform.core.logging.KVLogger
import jakarta.persistence.EntityManager
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.jvm.optionals.getOrDefault

@Service
class ReaderService(
    private val viewDao: ViewRepository,
    private val readerDao: ReaderRepository,
    private val storage: TrackingStorageService,
    private val logger: KVLogger,
    private val em: EntityManager,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ReaderService::class.java)
    }

    fun findViewedStoryIds(userId: Long?, deviceId: String): List<Long> =
        viewDao.findStoryIdsByUserIdOrDeviceId(userId, deviceId)

    fun view(command: ViewStoryCommand) {
        logger.add("command", "ViewStoryCommand")
        logger.add("request_story_id", command.storyId)
        logger.add("request_user_id", command.userId)
        logger.add("request_device_id", command.deviceId)
        logger.add("request_timestamp", command.timestamp)
        logger.add("request_read_time_millis", command.readTimeMillis)

        viewDao.save(
            ViewEntity(
                userId = command.userId,
                deviceId = command.deviceId,
                storyId = command.storyId,
            ),
        )
    }

    fun search(request: SearchReaderRequest): List<ReaderEntity> {
        logger.add("command", "SearchReaderQuery")
        logger.add("request_story_id", request.storyId)
        logger.add("request_subscribed_to_user_id", request.subscribedToUserId)
        logger.add("request_limit", request.limit)
        logger.add("request_offset", request.offset)

        val builder = SearchReaderQueryBuilder()
        val sql = builder.query(request)
        val params = builder.parameters(request)
        val query = em.createNativeQuery(sql, ReaderEntity::class.java)
        Predicates.setParameters(query, params)
        return query.resultList as List<ReaderEntity>
    }

    @Transactional
    fun onCommented(userId: Long, storyId: Long) {
        storeReader(userId, storyId, commented = true)
    }

    @Transactional
    fun onLiked(userId: Long, storyId: Long) {
        storeReader(userId, storyId, liked = true)
    }

    @Transactional
    fun onUnliked(userId: Long, storyId: Long) {
        storeReader(userId, storyId, liked = false)
    }

    @Transactional
    fun onSubscribed(userId: Long, storyId: Long) {
        storeReader(userId, storyId, subscribed = true)
    }

    @Transactional
    fun countSubscriberReaders(story: StoryEntity): Long =
        readerDao.countSubscriberByStoryIdAndUserId(story.id!!, story.userId) ?: 0

    private fun importEmails(file: File): Long {
        var result = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("account_id", "product_id", "read_count")
                .build(),
        )
        parser.use {
            for (record in parser) {
                try {
                    val userId = record.get(0)?.ifEmpty { null }?.toLong() ?: continue
                    val storyId = record.get(1)?.toLong() ?: continue

                    storeReader(userId, storyId, email = true)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unexpected error", ex)
                }
            }
            return result
        }
    }

    @Transactional
    fun storeReader(
        userId: Long,
        storyId: Long,
        commented: Boolean? = null,
        liked: Boolean? = null,
        subscribed: Boolean? = null,
        email: Boolean? = null,
    ): ReaderEntity {
        val reader = readerDao.findByUserIdAndStoryId(userId, storyId)
            .getOrDefault(
                ReaderEntity(
                    userId = userId,
                    storyId = storyId,
                ),
            )

        if (commented != null) {
            reader.commented = commented
        }
        if (liked != null) {
            reader.liked = liked
        }
        if (subscribed != null) {
            reader.subscribed = subscribed
        }
        if (email != null) {
            reader.email = email
        }
        return readerDao.save(reader)
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
