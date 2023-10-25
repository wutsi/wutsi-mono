package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.DurationEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL

abstract class AbstractDurationRepository : AbstractRepository<DurationEntity>() {
    companion object {
        private val HEADERS = arrayOf(
            "correlation_id",
            "product_id",
            "total_seconds",
        )
    }

    fun filename(): String = "durations.csv"

    override fun accept(url: URL): Boolean =
        url.file.endsWith("/" + filename())

    override fun read(input: InputStream): List<DurationEntity> {
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
            DurationEntity(
                correlationId = get(it, "correlation_id") ?: "",
                productId = get(it, "product_id") ?: "",
                totalMinutes = get(it, "total_seconds")?.toLong() ?: -1,
            )
        }
    }

    override fun storeLocally(items: List<DurationEntity>, out: OutputStream) {
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
                        it.correlationId,
                        it.productId,
                        it.totalMinutes,
                    )
                }
                printer.flush()
            }
        }
    }
}
