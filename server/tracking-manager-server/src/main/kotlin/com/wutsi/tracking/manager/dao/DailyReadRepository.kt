package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.ReadEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class DailyReadRepository : AbstractRepository<ReadEntity>() {
    companion object {
        private val HEADERS = arrayOf(
            "product_id",
            "total_reads",
        )
    }

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
                totalReads = get(it, "total_reads")?.toLong() ?: -1,
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

    override fun getURLs(date: LocalDate): List<URL> {
        val urls = mutableListOf<URL>()
        val visitor = createVisitor(urls)
        storage.visit(getStorageFolder(date), visitor)
        return urls
    }

    override fun getStorageFolder(date: LocalDate): String =
        "kpi/daily/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
}
