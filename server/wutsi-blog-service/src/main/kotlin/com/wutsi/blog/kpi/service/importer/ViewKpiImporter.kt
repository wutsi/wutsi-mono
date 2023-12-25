package com.wutsi.blog.kpi.service.importer

import com.wutsi.blog.kpi.dto.KpiType
import com.wutsi.blog.kpi.service.KpiPersister
import com.wutsi.blog.kpi.service.TrackingStorageService
import com.wutsi.blog.product.dto.SearchProductRequest
import com.wutsi.blog.product.service.ProductService
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
        val productIds = importProductKpis(date, file)
        val products = productService.searchProducts(
            SearchProductRequest(
                productIds = productIds.toList(),
                limit = productIds.size
            )
        )
        products.forEach { product -> productService.onKpiImported(product) }
        return products.size.toLong()
    }

    private fun importProductKpis(date: LocalDate, file: File): Collection<Long> {
        val parser = CSVParser.parse(
            file.toPath(),
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader("product_id", "total_views")
                .build(),
        )

        val productIds = mutableSetOf<Long>()
        parser.use {
            for (record in parser) {
                val productId = record.get("product_id")?.ifEmpty { null }?.trim()
                val totalViews = record.get("total_views")?.ifEmpty { null }?.trim()

                try {
                    persister.persistProduct(
                        date,
                        type = KpiType.VIEW,
                        productId = productId!!.toLong(),
                        value = totalViews!!.toLong(),
                    )
                    productIds.add(productId.toLong())
                } catch (ex: Exception) {
                    LOGGER.warn("Unable to persist product KPI - productId=$productIds", ex)
                }
            }
        }
        return productIds
    }
}
