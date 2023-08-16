package com.wutsi.tracking.manager.dao

import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.tracking.manager.entity.SourceEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL

abstract class AbstractSourceRepository : AbstractRepository<SourceEntity>() {
    companion object {
        private val HEADERS = arrayOf(
            "product_id",
            "source",
            "total_reads",
        )
    }

    fun filename(): String = "source.csv"

    override fun accept(url: URL): Boolean =
        url.file.endsWith("/" + filename())

    override fun read(input: InputStream): List<SourceEntity> {
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
            SourceEntity(
                productId = get(it, "product_id") ?: "",
                source = try {
                    TrafficSource.valueOf(get(it, "source") ?: TrafficSource.UNKNOWN.name)
                } catch (ex: Exception) {
                    TrafficSource.UNKNOWN
                },
                totalReads = get(it, "total_reads")?.toLong() ?: -1,
            )
        }
    }

    override fun storeLocally(items: List<SourceEntity>, out: OutputStream) {
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
                        it.source,
                        it.totalReads,
                    )
                }
                printer.flush()
            }
        }
    }
}
