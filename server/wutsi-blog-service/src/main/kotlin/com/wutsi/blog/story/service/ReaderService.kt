package com.wutsi.blog.story.service

import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.ReaderRepository
import com.wutsi.blog.story.dao.StoryRepository
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.ReaderEntity
import com.wutsi.blog.story.domain.ViewEntity
import com.wutsi.blog.story.dto.ViewStoryCommand
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

@Service
class ReaderService(
    private val viewDao: ViewRepository,
    private val readerDao: ReaderRepository,
    private val storyDao: StoryRepository,
    private val storage: TrackingStorageService,
    private val logger: KVLogger,
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

    fun importMonthlyReaders(date: LocalDate): Long {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/readers.csv"
        LOGGER.info(date.format(DateTimeFormatter.ofPattern("yyyy-MM")) + " - Importing Monthly Readers from $path")

        return try {
            importReaders(path)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to log KPIs for $date from $path", ex)
            0L
        }
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

    private fun importReaders(path: String): Long {
        val file = downloadTrackingFile(path)
        val storyIds = mutableSetOf<Long>()
        try {
            val result = importReaders(file, storyIds)
            storyIds.forEach {
                updateStoryStats(it)
            }
            return result
        } finally {
            file.delete()
        }
    }

    private fun updateStoryStats(storyId: Long) {
        LOGGER.info(">>> Update Stats of Story#$storyId")
        val story = storyDao.findById(storyId).getOrNull() ?: return

        story.subscriberReaderCount = readerDao.countSubscriberByStoryIdAndUserId(storyId, story.userId) ?: 0
        story.modificationDateTime = Date()
        storyDao.save(story)
    }

    private fun importReaders(file: File, storyIds: MutableSet<Long>): Long {
        var result = 0L
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("account_id", "device_id", "product_id", "read_count")
                .build(),
        )
        parser.use {
            val readers = mutableMapOf<String, ReaderEntity>()
            for (record in parser) {
                try {
                    val storyId = record.get(2)?.toLong() ?: continue
                    val userId = record.get(0)?.ifEmpty { null }?.toLong()
                    val deviceId = record.get(1)

                    storeView(userId, deviceId, storyId)
                    userId?.let { storeReader(userId, storyId, readers) }

                    storyIds.add(storyId)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unexpected error", ex)
                }
            }
            return result
        }
    }

    private fun storeView(userId: Long?, deviceId: String, storyId: Long) {
        viewDao.save(
            ViewEntity(
                userId = userId,
                deviceId = deviceId,
                storyId = storyId,
            ),
        )
    }

    private fun storeReader(
        userId: Long,
        storyId: Long,
        readers: MutableMap<String, ReaderEntity>,
    ): ReaderEntity {
        val key = "$userId-$storyId"
        if (readers.contains(key)) {
            return readers[key]!!
        }

        val reader = storeReader(userId, storyId)
        readers[key] = reader
        return reader
    }

    private fun storeReader(
        userId: Long,
        storyId: Long,
        commented: Boolean? = null,
        liked: Boolean? = null,
        subscribed: Boolean? = null,
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
