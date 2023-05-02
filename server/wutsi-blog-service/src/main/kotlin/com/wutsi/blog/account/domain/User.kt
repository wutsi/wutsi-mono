package com.wutsi.blog.account.domain

import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_USER")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String = "",
    var fullName: String = "",
    var email: String? = null,
    var pictureUrl: String? = null,
    var biography: String? = null,
    var loginCount: Long = 0,
    var lastLoginDateTime: Date? = null,
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
    var blog: Boolean = false,
    var storyCount: Long = 0,
    var followerCount: Long = 0,
    var subscriberCount: Long = 0,
    val autoFollowByBlogs: Boolean? = false,
    var lastPublicationDateTime: Date? = null,
    val testUser: Boolean = false,
    val siteId: Long = -1,
    val suspended: Boolean = false,
)
