package com.wutsi.application.feed.service

import com.wutsi.application.feed.model.ProductModel
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.OutputStream
import java.io.OutputStreamWriter

abstract class AbstractProductWriter {
    fun write(items: List<ProductModel>, out: OutputStream) {
        val writer = OutputStreamWriter(out)
        writer.use {
            val printer = CSVPrinter(
                writer,
                CSVFormat.Builder
                    .create()
                    .setHeader(*toHeaders())
                    .build(),
            )
            printer.use {
                items.forEach {
                    printer.printRecord(*toRecord(it))
                }
            }
        }
    }

    protected abstract fun toHeaders(): Array<String>
    protected abstract fun toRecord(item: ProductModel): Array<String?>
}
