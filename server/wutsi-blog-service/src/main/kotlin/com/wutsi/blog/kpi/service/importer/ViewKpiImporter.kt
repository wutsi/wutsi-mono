package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.product.service.ProductService
import com.wutsi.blog.story.dto.SearchStoryRequest
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ViewKpiImporter(
    storage: TrackingStorageService,
    persister: KpiPersister,
    private val productService: ProductService,
) : AbstractImporter(storage, persister) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ViewKpiImporter::class.java)
    }

    override fun getFilePath(date: LocalDate) =
        "kpi/monthly/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM")) + "/views.csv"

    override fun import(date: LocalDate, file: File): Long {
        val storyIds = importStoryKpis(date, file)

        // Update user counters
        val products = productService.searchProducts(
            request = SearchStoryRequest(
                storyIds = storyIds.toList(),
                limit = storyIds.size,
            ),
        )
        val userIds = stories.map { it.userId }.toSet()
        aggregateUserKpis(date, KpiType.READ, userIds, listOf(TrafficSource.ALL))

        return storyIds.size.toLong()
    }

    private fun importStoryKpis(date: LocalDate, file: File): Collection<Long> {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("product_id", "total_reads")
                .build(),
        )

        val storyIds = mutableSetOf<Long>()
        parser.use {
            for (record in parser) {
                val storyId = record.get("product_id")?.ifEmpty { null }?.trim()
                val totalReads = record.get("total_reads")?.ifEmpty { null }?.trim()

                try {
                    persister.persistStory(
                        date,
                        type = KpiType.READ,
                        storyId = storyId!!.toLong(),
                        value = totalReads!!.toLong(),
                    )
                    storyIds.add(storyId.toLong())
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist story KPI - storyId=$storyId", ex)
                }
            }
        }
        return storyIds
    }
}
