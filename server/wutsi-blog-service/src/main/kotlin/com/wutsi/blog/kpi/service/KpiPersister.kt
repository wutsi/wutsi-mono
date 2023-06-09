package com.wutsi.blog.kpi.service

import com.wutsi.blog.kpi.domain.KpiType
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.persistence.EntityManager

@Service
class KpiService(
    private val storage: TrackingStorageService,
    private val em: EntityManager,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(KpiService::class.java)
    }

    fun importMonthlyReads(date: LocalDate): Long {
        val path = "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/reads.csv"
        try {
            val file = downloadTrackingFile(path)
            try {
                return importMonthlyReads(date, file)
            } finally {
                file.delete()
            }
        } catch (ex: Exception) {
            LOGGER.warn("Unable to log KPIs for $date from $path", ex)
            return 0
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
                    importReads(date, record)
                    result++
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to line $record", ex)
                }
            }
            return result
        }
    }

    private fun importReads(date: LocalDate, record: CSVRecord) {
        val storyId = record.get("product_id")?.toLong() ?: 0
        val value = record.get("total_reads")?.toLong() ?: 0
        val sql = """
            INSERT INTO T_KPI_MONTHLY(story_id, type, year, month, value)
                VALUES($storyId, ${KpiType.READ.ordinal}, ${date.year}, ${date.month.value}, $value)
                ON DUPLICATE KEY UPDATE value=$value
        """.trimIndent()

        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.prepareStatement(sql)
            stmt.use {
                val result = stmt.executeUpdate().toLong()

                LOGGER.info("$result KPIs computed from Orders")
                return result
            }
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
