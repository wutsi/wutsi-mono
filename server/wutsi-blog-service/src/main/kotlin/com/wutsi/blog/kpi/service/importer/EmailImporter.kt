package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.story.service.ReaderService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class EmailImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val readerService: ReaderService,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EmailImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/emails.csv"

    override fun import(date: LocalDate, file: File): Long {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("account_id", "product_id", "total_reads")
                .build(),
        )

        val kpis: MutableList<KpiEmail> = mutableListOf()
        parser.use {
            for (record in parser) {
                kpis.add(
                    KpiEmail(
                        userId = record.get("account_id")?.ifEmpty { null }?.trim(),
                        storyId = record.get("product_id")?.ifEmpty { null }?.trim(),
                        totalReads = record.get("total_reads")?.ifEmpty { null }?.trim(),
                    )
                )
            }
        }

        val map = kpis.groupBy { it.storyId }
        map.keys
            .mapNotNull { it }
            .forEach { storyId ->
                try {
                    // Persist the KPI
                    val id = storyId.toLong()
                    val value = map[storyId]?.let { kpis -> uniqueCount(kpis) } ?: 0
                    persister.persistStory(date, KpiType.READER_EMAIL, id, value)

                    // Update the reader
                    map[storyId]?.let {
                        storeReaders(id, it)
                    }
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }

        return kpis.size.toLong()
    }

    private fun storeReaders(storyId: Long, kpis: List<KpiEmail>) {
        val userIds = kpis.mapNotNull { it.userId }.toSet()
        userIds.forEach { userId ->
            try {
                readerService.storeReader(userId.toLong(), storyId, email = true)
            } catch (ex: Exception) {
                LOGGER.warn("Unable to update Reader - storyId=$storyId, userId=$userId", ex)
            }
        }
    }

    private fun uniqueCount(kpis: List<KpiEmail>): Long =
        kpis.map { it.userId }.toSet().size.toLong()
}

private data class KpiEmail(
    val userId: String?,
    val storyId: String?,
    val totalReads: String?,
)
