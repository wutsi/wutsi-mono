package com.wutsi.tracking.manager.dao

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.storage.StorageVisitor
import com.wutsi.tracking.manager.entity.LegacyTrackEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import java.io.InputStream
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class LegacyTrackRepository(
    private val storage: StorageService,
) {
    companion object {
        private val HEADERS = arrayOf(
            "time",
            "hitid",
            "deviceid",
            "userid",
            "page",
            "event",
            "productid",
            "value",
            "os",
            "osversion",
            "devicetype",
            "browser",
            "ip",
            "long",
            "lat",
            "traffic",
            "referer",
            "bot",
            "ua",
            "source",
            "medium",
            "campaign",
            "url",
            "siteid",
            "impressions",
        )
    }

    fun read(input: InputStream): List<LegacyTrackEntity> {
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
            LegacyTrackEntity(
                time = get(it, "time")?.let { value -> toLong(value) } ?: -1,
                hitId = get(it, "hitid"),
                deviceId = get(it, "deviceid"),
                userId = get(it, "userid"),
                page = get(it, "page"),
                event = get(it, "event"),
                productId = get(it, "productid"),
                value = get(it, "value"),
                ip = get(it, "ip"),
                latitude = get(it, "lat")?.let { value -> toDouble(value) },
                longitude = get(it, "long")?.let { value -> toDouble(value) },
                bot = get(it, "bot")?.let { value -> toBoolean(value) } ?: false,
                device = get(it, "devicetype"),
                browser = get(it, "browser"),
                source = get(it, "source"),
                campaign = get(it, "campaign"),
                medium = get(it, "medium"),
                url = get(it, "url"),
                referer = get(it, "referer"),
                impressions = get(it, "impressions"),
                os = get(it, "os"),
                siteid = get(it, "siteid"),
                trafficType = get(it, "traffic"),
                userAgent = get(it, "ua"),
            )
        }
    }

    private fun get(record: CSVRecord, name: String): String? =
        try {
            record.get(name)
        } catch (ex: Exception) {
            null
        }

    private fun toLong(str: String): Long =
        try {
            str.toLong()
        } catch (ex: Exception) {
            0L
        }

    private fun toDouble(str: String): Double? =
        try {
            str.toDouble()
        } catch (ex: Exception) {
            null
        }

    private fun toBoolean(str: String): Boolean =
        try {
            str.toBoolean()
        } catch (ex: Exception) {
            false
        }

    fun getURLs(date: LocalDate): List<URL> {
        val urls = mutableListOf<URL>()
        val visitor = createVisitor(urls)
        storage.visit(getStorageFolder(date), visitor)
        return urls
    }

    private fun createVisitor(urls: MutableList<URL>) = object : StorageVisitor {
        override fun visit(url: URL) {
            urls.add(url)
        }
    }

    private fun getStorageFolder(date: LocalDate): String =
        "legacy/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
}
