package com.wutsi.tracking.manager.dao

import com.wutsi.tracking.manager.entity.CampaignEntity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL

abstract class AbstractCampaignRepository : AbstractRepository<CampaignEntity>() {
    companion object {
        private val HEADERS = arrayOf(
            "campaign",
            "total_impressions",
        )
    }

    fun filename(): String = "ads_impressions.csv"

    override fun accept(url: URL): Boolean =
        url.file.endsWith("/" + filename())

    override fun read(input: InputStream): List<CampaignEntity> {
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
            CampaignEntity(
                campaign = get(it, "campaign") ?: "",
                totalImpressions = get(it, "total_impressions")?.toLong() ?: 0,
            )
        }
    }

    override fun storeLocally(items: List<CampaignEntity>, out: OutputStream) {
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
                        it.campaign,
                        it.totalImpressions,
                    )
                }
                printer.flush()
            }
        }
    }
}
