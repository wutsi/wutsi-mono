package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.ViewEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL

abstract class AbstractViewRepository : AbstractRepository<ViewEntity>() {
    companion object {
        private val HEADERS = arrayOf(
            "product_id",
            "total_views",
        )
    }

    fun filename(): String = "views.csv"

    override fun accept(url: URL): Boolean =
        url.file.endsWith("/" + filename())

    override fun read(input: InputStream): List<ViewEntity> {
        val parser = CSVParser.parse(
            input,
            Charsets.UTF_8,
            CSVFormat.Builder.create()
                .setSkipHeaderRecord(true)
                .setDelimiter(",")
                .setHeader(*HEADERS)
                .setAllowMissingColumnNames(true)
                .build(),
        )
        return parser.map {
            ViewEntity(
                productId = get(it, "product_id") ?: "",
                totalViews = get(it, "total_views")?.toLong() ?: 0,
            )
        }
    }

    override fun storeLocally(items: List<ViewEntity>, out: OutputStream) {
        val writer = BufferedWriter(OutputStreamWriter(out))
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.DEFAULT
                    .builder()
                    .setHeader(*HEADERS)
                    .build(),
            )
            printer.use {
                items.forEach {
                    printer.printRecord(
                        it.productId,
                        it.totalViews,
                    )
                }
                printer.flush()
            }
        }
    }
}
