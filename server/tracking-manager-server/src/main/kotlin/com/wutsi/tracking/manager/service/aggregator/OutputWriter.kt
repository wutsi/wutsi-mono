package com.wutsi.tracking.manager.service.aggregator

import com.wutsi.platform.core.storage.StorageService
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.UUID

abstract class OutputWriter<K, V>(
    val path: String,
    val storage: StorageService,
) {
    abstract fun headers(): Array<String>
    abstract fun values(pair: KeyPair<K, V>): Array<Any>

    fun write(pairs: List<KeyPair<K, V>>) {
        if (pairs.isEmpty() && !isFileExists()) {
            return
        }

        val file = File.createTempFile(UUID.randomUUID().toString(), ".csv")
        try {
            write(pairs, file)
        } finally {
            file.delete()
        }
    }

    private fun write(pairs: List<KeyPair<K, V>>, file: File) {
        // Write locally
        val output = FileOutputStream(file)
        output.use {
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use {
                val printer = CSVPrinter(
                    writer,
                    CSVFormat.DEFAULT
                        .builder()
                        .setHeader(*headers())
                        .build(),
                )
                printer.use {
                    pairs.forEach {
                        printer.printRecord(*values(it))
                    }
                }
            }
        }

        // Write to cloud storage
        val input = FileInputStream(file)
        input.use {
            val url = storage.store(path, input, "text/csv", null, "utf-8")
            LoggerFactory.getLogger(this.javaClass).info(">>> Storing ${pairs.size} items to: $url")
        }
    }

    private fun isFileExists(): Boolean =
        storage.exists(storage.toURL(path))
}
