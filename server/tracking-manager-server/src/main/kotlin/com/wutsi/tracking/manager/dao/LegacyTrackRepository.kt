package com.wutsi.tracking.manager.dao

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.storage.StorageVisitor
import com.wutsi.tracking.manager.entity.LegacyTrackEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
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
                time = toLong(it.get("time")),
                hitId = it.get("hitid"),
                deviceId = it.get("deviceid"),
                userId = it.get("userid"),
                page = it.get("page"),
                event = it.get("event"),
                productId = it.get("productid"),
                value = it.get("value"),
                ip = it.get("ip"),
                latitude = toDouble(it.get("lat")),
                longitude = toDouble(it.get("long")),
                bot = it.get("bot").toBoolean(),
                device = it.get("devicetype"),
                browser = it.get("browser"),
                source = it.get("source"),
                campaign = it.get("campaign"),
                medium = it.get("medium"),
                url = it.get("url"),
                referer = it.get("referer"),
                impressions = it.get("impressions"),
                os = it.get("os"),
                siteid = it.get("siteid"),
                trafficType = it.get("traffic"),
                userAgent = it.get("ua"),
            )
        }
    }

    private fun toLong(str: String): Long =
        try {
            str.toLong()
        } catch (ex: Exception) {
            0L
        }

    private fun toDouble(str: String): Double =
        try {
            str.toDouble()
        } catch (ex: Exception) {
            0.0
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
        "track/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
}
