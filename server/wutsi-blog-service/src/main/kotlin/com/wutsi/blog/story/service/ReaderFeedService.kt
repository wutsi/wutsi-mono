package com.wutsi.blog.story.service

import com.wutsi.blog.story.dao.ReaderRepository
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.file.Files
import javax.transaction.Transactional

@Service
class ReaderFeedService(
    private val readerDao: ReaderRepository,
    private val storage: StorageService,
    private val logger: KVLogger,
) {
    companion object {
        private val HEADERS = arrayOf(
            "story_id",
            "user_id",
            "commented",
            "liked",
            "subscribed",
        )
    }

    @Transactional
    fun generate(): Long {
        val file = Files.createTempFile("readers", ".csv").toFile()
        val result = toCsv(file)

        val input = FileInputStream(file)
        input.use {
            val url = storage.store("feeds/readers.csv", input, "text/csv", null, "utf-8", file.length())
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
                    val readers = readerDao.findAll(PageRequest.of(offset / limit, limit))
                    if (readers.isEmpty()) {
                        break
                    }

                    readers.forEach { reader ->
                        printer.printRecord(
                            reader.storyId,
                            reader.userId,
                            if (reader.commented) "1" else null,
                            if (reader.liked) "1" else null,
                            if (reader.subscribed) "1" else null,
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
