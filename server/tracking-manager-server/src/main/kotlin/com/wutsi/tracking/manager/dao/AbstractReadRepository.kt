package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.ReadEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL

abstract class AbstractReadRepository : AbstractRepository<ReadEntity>() {
    companion object {
        private val HEADERS = arrayOf(
            "product_id",
            "total_reads",
        )
    }

    fun filename(): String = "reads.csv"

    override fun accept(url: URL): Boolean =
        url.file.endsWith("/" + filename())

    override fun read(input: InputStream): List<ReadEntity> {
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
            ReadEntity(
                productId = get(it, "product_id") ?: "",
                totalReads = get(it, "total_reads")?.toLong() ?: 0,
            )
        }
    }

    override fun storeLocally(items: List<ReadEntity>, out: OutputStream) {
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
                        it.totalReads,
                    )
                }
                printer.flush()
            }
        }
    }
}
