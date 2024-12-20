package com.wutsi.blog.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date

@Entity
@Table(name = "T_USER")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String = "",
    var fullName: String = "",
    var email: String? = null,
    var pictureUrl: String? = null,
    var biography: String? = null,

    val creationDateTime: Date = Date(),
    var modificationDateTime: Date = Date(),
    var websiteUrl: String? = null,
    val superUser: Boolean = false,
    var language: String? = null,
    var readAllLanguages: Boolean? = null,
    var facebookId: String? = null,
    var twitterId: String? = null,
    var linkedinId: String? = null,
    var youtubeId: String? = null,
    var whatsappId: String? = null,
    var telegramId: String? = null,
    var githubId: String? = null,
    var blog: Boolean = false,
    var lastLoginDateTime: Date? = null,

    var storyCount: Long = 0,
    var draftStoryCount: Long = 0,
    var publishStoryCount: Long = 0,
    var subscriberCount: Long = 0,
    var pinStoryId: Long? = null,
    var pinDateTime: Date? = null,
    var readCount: Long = 0,

    @Deprecated("")
    var followerCount: Long = 0,

    @Deprecated("")
    val autoFollowByBlogs: Boolean? = false,

    @Deprecated("")
    val siteId: Long = -1,

    @Deprecated("")
    var loginCount: Long = 0,

    var lastPublicationDateTime: Date? = null,
    val testUser: Boolean = false,

    val suspended: Boolean = false,
    var active: Boolean = true,
    var walletId: String? = null,
    var country: String? = null,
    var endorserCount: Long = 0,
    var totalDurationSeconds: Long = 0,
    var wpp: Boolean = false,
    var clickCount: Long = 0,
    var storeId: String? = null,
    var blogDateTime: Date? = null,
    var wppDateTime: Date? = null,
    var orderCount: Long = 0,
    var donationCount: Long = 0,
    var totalSales: Long = 0,
    var superFanCount: Long = 0,

    @Column(name = "category_fk")
    var categoryId: Long? = null,

    var lastWeeklyEmailSentDateTime: Date? = null,
    var lastWeeklyEmailOpenedDateTime: Date? = null,
)
