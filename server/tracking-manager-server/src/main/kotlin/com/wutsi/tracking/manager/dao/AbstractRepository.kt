package com.wutsi.tracking.manager.dao

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.storage.StorageVisitor
import com.wutsi.tracking.manager.Repository
import org.apache.commons.csv.CSVRecord
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.time.LocalDate
import java.util.UUID

abstract class AbstractRepository<I> : Repository<I> {
    @Autowired
    protected lateinit var storage: StorageService

    override fun getURLs(date: LocalDate): List<URL> {
        val urls = mutableListOf<URL>()
        val visitor = createVisitor(urls)
        storage.visit(getStorageFolder(date), visitor)
        return urls
    }

    protected abstract fun getStorageFolder(date: LocalDate): String

    protected abstract fun storeLocally(items: List<I>, out: OutputStream)

    override fun save(items: List<I>, date: LocalDate, filename: String): URL {
        val file = File.createTempFile(UUID.randomUUID().toString(), "csv")
        try {
            // Store to file
            val output = FileOutputStream(file)
            output.use {
                storeLocally(items, output)
            }

            // Store to cloud
            val input = FileInputStream(file)
            input.use {
                return storeToCloud(input, date, filename)
            }
        } finally {
            file.delete()
        }
    }

    private fun storeToCloud(input: InputStream, date: LocalDate, filename: String): URL {
        val folder = getStorageFolder(date)
        return storage.store("$folder/$filename", input, "text/csv", Int.MAX_VALUE)
    }

    protected fun createVisitor(urls: MutableList<URL>) = object : StorageVisitor {
        override fun visit(url: URL) {
            urls.add(url)
        }
    }

    protected fun get(record: CSVRecord, name: String): String? =
        try {
            record.get(name)
        } catch (ex: Exception) {
            null
        }

    protected fun toBoolean(str: String): Boolean =
        try {
            str.toBoolean()
        } catch (ex: Exception) {
            false
        }

    protected fun toLong(str: String): Long =
        try {
            str.toLong()
        } catch (ex: Exception) {
            0L
        }

    protected fun toDouble(str: String): Double =
        try {
            str.toDouble()
        } catch (ex: Exception) {
            0.0
        }
}
