package com.wutsi.tracking.manager.dao

import com.wutsi.platform.core.storage.StorageService
import com.wutsi.platform.core.storage.StorageVisitor
import com.wutsi.tracking.manager.entity.TrackEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class TrackRepository(
    private val storage: StorageService,
) {
    companion object {
        const val PATH_PREFIX = "track"

        private val HEADERS = arrayOf(
            "time",
            "correlation_id",
            "device_id",
            "account_id",
            "merchant_id",
            "product_id",
            "page",
            "event",
            "value",
            "revenue",
            "ip",
            "long",
            "lat",
            "bot",
            "device_type",
            "channel",
            "source",
            "campaign",
            "url",
            "referrer",
            "ua",
            "business_id",
        )
    }

    fun save(items: List<TrackEntity>, date: LocalDate = LocalDate.now()): URL {
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
                return storeToCloud(input, date)
            }
        } finally {
            file.delete()
        }
    }

    fun read(input: InputStream): List<TrackEntity> {
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
            TrackEntity(
                time = toLong(it.get("time")),
                correlationId = it.get("correlation_id"),
                deviceId = it.get("device_id"),
                accountId = it.get("account_id"),
                merchantId = it.get("merchant_id"),
                productId = it.get("product_id"),
                page = it.get("page"),
                event = it.get("event"),
                value = it.get("value"),
                revenue = toLong(it.get("revenue")),
                ip = it.get("ip"),
                lat = toDouble(it.get("lat")),
                long = toDouble(it.get("long")),
                bot = it.get("bot").toBoolean(),
                deviceType = it.get("device_type"),
                channel = it.get("channel"),
                source = it.get("source"),
                campaign = it.get("campaign"),
                url = it.get("url"),
                referrer = it.get("referrer"),
                ua = it.get("ua"),
                businessId = if (it.size() > 21) it.get("business_id") else null,
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

    private fun storeLocally(items: List<TrackEntity>, out: OutputStream) {
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
                        it.time,
                        it.correlationId,
                        it.deviceId,
                        it.accountId,
                        it.merchantId,
                        it.productId,
                        it.page,
                        it.event,
                        it.value,
                        it.revenue,
                        it.ip,
                        it.long,
                        it.lat,
                        it.bot,
                        it.deviceType,
                        it.channel,
                        it.source,
                        it.campaign,
                        it.url,
                        it.referrer,
                        it.ua,
                        it.businessId,
                    )
                }
                printer.flush()
            }
        }
    }

    private fun storeToCloud(input: InputStream, date: LocalDate): URL {
        val folder = getStorageFolder(date)
        val file = UUID.randomUUID().toString() + ".csv"
        return storage.store("$folder/$file", input, "text/csv", Int.MAX_VALUE)
    }

    private fun getStorageFolder(date: LocalDate): String =
        "$PATH_PREFIX/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
}
