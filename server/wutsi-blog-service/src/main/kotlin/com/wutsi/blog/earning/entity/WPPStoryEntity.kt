package com.wutsi.blog.earning.entity

data class WPPStoryEntity(
    val id: Long = -1,
    val userId: Long = -1,
    var wppScore: Double = 0.0,
    var readCount: Long = 0,
    var readerCount: Long = 0,
    var readTime: Long = 0,
    var subscriptionCount: Long = 0,
    var readRatio: Double = 0.0,
    var earningRatio: Double = 0.0,
    var earningAdjustment: Double = 0.0,
    var bonusRatio: Double = 0.0,
    var earnings: Long = 0,
    var bonus: Long = 0
)
