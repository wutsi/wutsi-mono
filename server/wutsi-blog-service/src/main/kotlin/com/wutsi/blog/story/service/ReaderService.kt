package com.wutsi.blog.story.service

import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.dao.ViewRepository
import com.wutsi.blog.story.domain.ViewEntity
import com.wutsi.blog.story.dto.ViewStoryCommand
import com.wutsi.platform.core.logging.KVLogger
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
class ViewService(
    private val viewDao: ViewRepository,
    private val storage: TrackingStorageService,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ViewService::class.java)
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
            val file = downloadTrackingFile(path)
            try {
                return importMonthlyReaders(file)
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to log KPIs for $date from $path", ex)
            0L
        }
    }

    private fun importMonthlyReaders(file: File): Long {
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
            for (record in parser) {
                try {
                    viewDao.save(
                        ViewEntity(
                            userId = record.get(0)?.ifEmpty { null }?.toLong(),
                            deviceId = record.get(1),
                            storyId = record.get(2)?.toLong() ?: -1,
                        ),
                    )

                    result++
                } catch (ex: Exception) {
                    logger.setException(ex)
                }
            }
            return result
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
