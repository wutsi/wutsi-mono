package com.wutsi.blog.user.dto

import java.util.Date

data class User(
    val id: Long = -1,
    val name: String = "",
    val fullName: String = "",
    val biography: String? = null,
    val email: String? = null,
    val pictureUrl: String? = null,
    val language: String? = null,
    val lastLoginDateTime: Date? = null,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val websiteUrl: String? = null,
    val superUser: Boolean = false,
    val readAllLanguages: Boolean? = null,
    val facebookId: String? = null,
    val twitterId: String? = null,
    val linkedinId: String? = null,
    val youtubeId: String? = null,
    val githubId: String? = null,
    val whatsappId: String? = null,
    val telegramId: String? = null,
    val blog: Boolean = false,
    val lastPublicationDateTime: Date? = null,
    val testUser: Boolean = false,
    val storyCount: Long = 0L,
    val draftStoryCount: Long = 0,
    val publishStoryCount: Long = 0,
    val subscriberCount: Long = 0,
    val subscribed: Boolean = false,
    val pinStoryId: Long? = null,
    val readCount: Long = 0,
    val clickCount: Long = 0,
    val active: Boolean = true,
    val walletId: String? = null,
    val country: String? = null,
    val endorserCount: Long = 0,
    val totalDurationSeconds: Long = 0,
    val wpp: Boolean = false,
    val storeId: String? = null,
    val blogDateTime: Date? = null,
    val wppDateTime: Date? = null,
    val orderCount: Long = 0,
    val donationCount: Long = 0,
    val totalSales: Long = 0,
    val superFanCount: Long = 0,
    val categoryId: Long? = null,
    val preferredCategoryIds: List<Long> = emptyList(),
)
