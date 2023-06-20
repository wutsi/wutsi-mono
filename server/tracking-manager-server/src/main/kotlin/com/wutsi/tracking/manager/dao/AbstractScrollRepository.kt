package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.ScrollEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL

abstract class AbstractScrollRepository : AbstractRepository<ScrollEntity>() {
    companion object {
        private val HEADERS = arrayOf(
            "product_id",
            "average_scroll",
        )
    }

    fun filename(): String = "scrolls.csv"

    override fun accept(url: URL): Boolean =
        url.file.endsWith("/" + filename())

    override fun read(input: InputStream): List<ScrollEntity> {
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
            ScrollEntity(
                productId = get(it, "product_id") ?: "",
                averageScroll = get(it, "average_scroll")?.toLong() ?: 0,
            )
        }
    }

    override fun storeLocally(items: List<ScrollEntity>, out: OutputStream) {
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
                        it.averageScroll,
                    )
                }
                printer.flush()
            }
        }
    }
}
