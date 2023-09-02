package com.wutsi.blog.user.service

import com.wutsi.blog.user.dto.SearchUserRequest
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.file.Files

@Service
class UserFeedService(
    private val userService: UserService,
    private val storage: StorageService,
    private val logger: KVLogger,
) {
    companion object {
        private val HEADERS = arrayOf(
            "id",
        )
    }

    @Transactional
    fun generate(): Long {
        val file = Files.createTempFile("users", ".csv").toFile()
        val result = toCsv(file)

        val input = FileInputStream(file)
        input.use {
            val url = storage.store("feeds/users.csv", input, "text/csv", null, "utf-8", file.length())
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
                    val users = userService.search(
                        SearchUserRequest(
                            offset = offset,
                            limit = limit,
                        ),
                    )
                    if (users.isEmpty()) {
                        break
                    }

                    users.forEach { user ->
                        printer.printRecord(
                            user.id,
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
