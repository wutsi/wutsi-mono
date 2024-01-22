package com.wutsi.blog.user.dto

import java.util.Date

data class UserSummary(
    val id: Long = -1,
    val siteId: Long = -1,
    val name: String = "",
    val blog: Boolean = false,
    val fullName: String = "",
    val pictureUrl: String? = null,
    val biography: String? = null,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val storyCount: Long = 0L,
    val draftStoryCount: Long = 0,
    val publishStoryCount: Long = 0,
    val subscriberCount: Long = 0L,
    val subscribed: Boolean = false,
    val testUser: Boolean = false,
    val email: String? = null,
    val readCount: Long = 0,
    val endorserCount: Long = 0,
    val totalDurationSeconds: Long = 0,
    val clickCount: Long = 0,
    val wpp: Boolean = false,
    val language: String? = null,
    val country: String? = null,
    val walletId: String? = null,
    val storeId: String? = null,
    val orderCount: Long = 0,
    val donationCount: Long = 0,
    val totalSales: Long = 0,
)
