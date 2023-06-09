package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.TrackEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class TrackRepository : AbstractRepository<TrackEntity>() {
    companion object {
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

    override fun read(input: InputStream): List<TrackEntity> {
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

    override fun storeLocally(items: List<TrackEntity>, out: OutputStream) {
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

    override fun getStorageFolder(date: LocalDate): String =
        "track/" + date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
}
