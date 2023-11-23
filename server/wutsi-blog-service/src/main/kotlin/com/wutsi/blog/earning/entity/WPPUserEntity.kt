package com.wutsi.blog.earning.entity

import org.apache.commons.csv.CSVPrinter

data class WPPUserEntity(
    val userId: Long = -1,
    var earnings: Long = 0,
    var bonus: Long = 0,
) : CSVAware {
    companion object {
        fun csvHeader(): Array<String> {
            return arrayOf(
                "user_id",
                "earnings",
                "bonus",
                "total",
            )
        }
    }

    val total: Long
        get() = earnings + bonus

    override fun printCSV(printer: CSVPrinter) {
        printer.printRecord(
            userId,
            earnings,
            bonus,
            total,
        )
    }
}
