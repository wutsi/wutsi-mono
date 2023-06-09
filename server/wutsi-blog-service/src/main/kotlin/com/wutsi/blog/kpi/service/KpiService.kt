package com.wutsi.blog.kpi.service

import com.wutsi.blog.kpi.dto.KpiType
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
class KpiService(
    private val storage: TrackingStorageService,
    private val persister: KpiPersister,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiService::class.java)
    }

    fun replay(year: Int, month: Int? = null) {
        val now = LocalDate.now()

        var date = LocalDate.of(year, month ?: 1, 1)
        while (true) {
            importMonthlyReads(date)

            date = date.plusMonths(1)
            if (date.isAfter(now) || date.year > year || (month != null && date.month.value > month)) {
                break
            }
        }
    }

    fun importMonthlyReads(date: LocalDate): Long {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/reads.csv"
        return try {
            val file = downloadTrackingFile(path)
            try {
                importMonthlyReads(date, file)
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to log KPIs for $date from $path", ex)
            0L
        }
    }

    private fun importMonthlyReads(date: LocalDate, file: File): Long {
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
                try {
                    val storyId = record.get(0)?.trim()?.toLong() ?: 0
                    val value = record.get(1)?.trim()?.toLong() ?: 0

                    persister.persist(date, KpiType.READ, storyId, value)
                    persister.updateStory(storyId, KpiType.READ)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to line $record", ex)
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
