package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.EmailEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL

abstract class AbstractEmailRepository : AbstractRepository<EmailEntity>() {
    companion object {
        private val HEADERS = arrayOf(
            "account_id",
            "product_id",
            "total_reads",
        )
    }

    fun filename(): String = "emails.csv"

    override fun accept(url: URL): Boolean =
        url.file.endsWith("/" + filename())

    override fun read(input: InputStream): List<EmailEntity> {
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
            EmailEntity(
                accountId = get(it, "account_id") ?: "",
                productId = get(it, "product_id") ?: "",
                totalReads = get(it, "total_reads")?.toLong() ?: 0,
            )
        }
    }

    override fun storeLocally(items: List<EmailEntity>, out: OutputStream) {
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
                        it.accountId,
                        it.productId,
                        it.totalReads,
                    )
                }
                printer.flush()
            }
        }
    }
}
