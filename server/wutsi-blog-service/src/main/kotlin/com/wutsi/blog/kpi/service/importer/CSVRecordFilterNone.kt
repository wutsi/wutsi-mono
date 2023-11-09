package com.wutsi.blog.kpi.service.importer

import org.apache.commons.csv.CSVRecord

class CSVRecordFilterNone : CSVRecordFilter {
    override fun accept(record: CSVRecord): Boolean = true
}
