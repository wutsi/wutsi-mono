package com.wutsi.blog.earning.entity

import org.apache.commons.csv.CSVPrinter

data class WPPStoryEntity(
    val id: Long = -1,
    val userId: Long = -1,
    var wppScore: Double = 0.0,
    var readCount: Long = 0,
    var readerCount: Long = 0,
    var readTime: Long = 0,
    var likeCount: Long = 0,
    var commentCount: Long = 0,
    var clickCount: Long = 0,
    var readRatio: Double = 0.0,
    var earningRatio: Double = 0.0,
    var earningAdjustment: Double = 0.0,
    var earnings: Long = 0,
    var engagementRatio: Double = 0.0,
    var bonus: Long = 0
) : CSVAware {
    companion object {
        fun csvHeader(): Array<String> {
            return arrayOf(
                "story_id",
                "user_id",
                "read_count",
                "reader_count",
                "like_count",
                "comment_count",
                "click_count",
                "read_time",
                "earning_ratio",
                "earning_adjustment",
                "engagement_ratio",
                "earnings",
                "bonus",
                "total"
            )
        }
    }

    val engagementCount: Long
        get() = clickCount + commentCount + likeCount

    val total: Long
        get() = earnings + bonus

    override fun printCSV(printer: CSVPrinter) {
        printer.printRecord(
            id,
            userId,
            readCount,
            readerCount,
            likeCount,
            commentCount,
            clickCount,
            readTime,
            earningRatio,
            earningAdjustment,
            engagementRatio,
            earnings,
            bonus,
            total
        )
    }
}
