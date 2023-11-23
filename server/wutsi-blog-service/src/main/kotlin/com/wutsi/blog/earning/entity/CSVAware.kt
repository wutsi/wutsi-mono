package com.wutsi.blog.earning.entity

import org.apache.commons.csv.CSVPrinter

interface CSVAware {
    fun printCSV(printer: CSVPrinter)
}
