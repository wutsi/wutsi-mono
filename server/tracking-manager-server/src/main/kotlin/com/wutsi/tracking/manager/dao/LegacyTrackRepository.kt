package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.LegacyTrackEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Service
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class LegacyTrackRepository : AbstractRepository<LegacyTrackEntity>() {
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

    override fun read(input: InputStream): List<LegacyTrackEntity> {
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

    override fun storeLocally(items: List<LegacyTrackEntity>, out: OutputStream) {
        TODO("NOT SUPPORTED")
    }

    override fun getURLs(date: LocalDate): List<URL> {
        val urls = mutableListOf<URL>()
        val visitor = createVisitor(urls)
        storage.visit(getStorageFolder(date), visitor)
        return urls
    }

    override fun getStorageFolder(date: LocalDate): String =
        "legacy/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
}
