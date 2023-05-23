package com.wutsi.blog.app.page.settings.model

import java.util.Locale

data class UserModel(
    val id: Long = -1,
    val name: String = "",
    val fullName: String = "",
    val email: String? = null,
    val loginCount: Long = 0,
    val pictureUrl: String? = null,
    val pictureSmallUrl: String? = null,
    val websiteUrl: String? = null,
    val biography: String? = null,
    val facebookUrl: String? = null,
    val twitterUrl: String? = null,
    val linkedinUrl: String? = null,
    val youtubeUrl: String? = null,
    val whatsappUrl: String? = null,
    val messengerUrl: String? = null,
    val telegramUrl: String? = null,
    val rssUrl: String? = null,
    val slug: String = "",
    val superUser: Boolean = false,
    val language: String? = null,
    val locale: Locale? = null,
    val readAllLanguages: Boolean? = null,
    val facebookId: String? = null,
    val twitterId: String? = null,
    val linkedinId: String? = null,
    val youtubeId: String? = null,
    val whatsappId: String? = null,
    val telegramId: String? = null,
    val hasSocialLinks: Boolean = false,
    val hasInstantMessagingLinks: Boolean = false,
    val hasMessengerLinks: Boolean = false,
    val blog: Boolean = true,
    val storyCount: Long = 0,
    val followerCount: Long = 0,
    val subscriberCount: Long = 0,
    val followerCountText: String = "",
    val subscriberCountText: String = "",
    val testUser: Boolean = false,
) {
    fun hasFollowers(): Boolean =
        followerCount > 0

    fun hasSubscribers(): Boolean =
        subscriberCount > 0
}
