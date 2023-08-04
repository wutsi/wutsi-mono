package com.wutsi.blog.like.service

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
import java.nio.file.Files
import java.text.SimpleDateFormat
import javax.transaction.Transactional

@Service
class LikeFeedService(
    private val likeService: LikeService,
    private val storage: StorageService,
    private val logger: KVLogger,
) {
    companion object {
        private val HEADERS = arrayOf(
            "story_id",
            "user_id",
            "device_id",
            "like_date",
        )
    }

    @Transactional
    fun generate(): Long {
        val file = Files.createTempFile("likes", ".csv").toFile()
        val result = toCsv(file)

        val input = FileInputStream(file)
        input.use {
            val url = storage.store("feeds/likes.csv", input, "text/csv", null, "utf-8", file.length())
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
                while (true) {
                    val likes = likeService.all(limit, offset)
                    if (likes.isEmpty()) {
                        break
                    }

                    likes.forEach { like ->
                        printer.printRecord(
                            like.storyId,
                            like.userId,
                            like.deviceId,
                            SimpleDateFormat("yyyy-MM-dd").format(like.timestamp),
                        )
                        result++
                    }

                    offset += limit
                }
            }
        }
        return result
    }
}
