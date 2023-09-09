package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.NumberUtils
import com.wutsi.blog.country.dto.Country
import java.util.Locale

data class UserModel(
    val id: Long = -1,
    val name: String = "",
    val fullName: String = "",
    val email: String? = null,
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
    val githubUrl: String? = null,
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
    val githubId: String? = null,
    val hasSocialLinks: Boolean = false,
    val hasInstantMessagingLinks: Boolean = false,
    val hasMessengerLinks: Boolean = false,
    val blog: Boolean = true,
    val storyCount: Long = 0,
    val subscriberCount: Long = 0,
    val subscribed: Boolean = false,
    val testUser: Boolean = false,
    val draftStoryCount: Long = 0,
    val publishStoryCount: Long = 0,
    val pinStoryId: Long? = null,
    val readCount: Long = 0,
    val walletId: String? = null,
    val donationUrl: String? = null,
    val url: String? = null,
    val aboutUrl: String? = null,
    val country: String? = null,
) {
    val subscriberCountText: String
        get() = NumberUtils.toHumanReadable(subscriberCount)

    val readCountText: String
        get() = NumberUtils.toHumanReadable(readCount)

    val canEnableMonetization: Boolean
        get() = walletId == null && (country != null && Country.all.find { it.code == country } != null)

    fun canSubscribeTo(blog: UserModel): Boolean =
        blog.blog && (blog.id != id) && !blog.subscribed

    fun canDonateTo(blog: UserModel): Boolean =
        blog.blog && (blog.id != id) && (blog.walletId != null)

    fun canPin(story: StoryModel): Boolean =
        superUser || (story.user.id == id)

    fun canViewKpis(story: StoryModel): Boolean =
        superUser || (story.user.id == id)
}
