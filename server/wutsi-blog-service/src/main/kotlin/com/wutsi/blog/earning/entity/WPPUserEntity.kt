package com.wutsi.blog.earning.entity

import org.apache.commons.csv.CSVPrinter

data class WPPUserEntity(
    val userId: Long = -1,
    val userName: String? = null,
    val fullName: String? = null,
    val phoneNumber: String? = null,
    var earnings: Long = 0,
    var bonus: Long = 0,
) : CSVAware {
    companion object {
        fun csvHeader(): Array<String> = arrayOf(
                "user_id",
                "user_name",
                "full_name",
                "phone_number",
                "earnings",
                "bonus",
                "total",
            )
    }

    val total: Long
        get() = earnings + bonus

    override fun printCSV(printer: CSVPrinter) {
        printer.printRecord(
            userId,
            userName,
            fullName,
            phoneNumber,
            earnings,
            bonus,
            total,
        )
    }
}
