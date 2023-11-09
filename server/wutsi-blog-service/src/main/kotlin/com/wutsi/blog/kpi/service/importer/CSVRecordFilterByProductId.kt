package com.wutsi.blog.kpi.service.importer

import org.apache.commons.csv.CSVRecord

class CSVRecordFilterByProductId(private val storyIds: Collection<Long>) : CSVRecordFilter {
    override fun accept(record: CSVRecord): Boolean =
        try {
            val storyId = record.get("product_id")?.ifEmpty { null }?.trim()?.toLong()
            storyIds.contains(storyId)
        } catch (ex: Exception) {
            false
        }
}
