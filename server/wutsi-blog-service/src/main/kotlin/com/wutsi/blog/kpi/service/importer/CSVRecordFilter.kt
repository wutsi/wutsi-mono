package com.wutsi.blog.kpi.service.importer

import org.apache.commons.csv.CSVRecord

interface CSVRecordFilter {
    fun accept(record: CSVRecord): Boolean
}
