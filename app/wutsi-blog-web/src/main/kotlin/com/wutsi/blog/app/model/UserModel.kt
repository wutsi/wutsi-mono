package com.wutsi.blog.app.model

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
    val subscriberCount: Long = 0,
    val subscriberCountText: String = "",
    val subscribed: Boolean = false,
    val testUser: Boolean = false,
    val draftStoryCount: Long = 0,
    val publishStoryCount: Long = 0,
    val pinStoryId: Long? = null,
    val readCount: Long = 0,
    val readCountText: String = "",
    val walletId: String? = null,
) {
    fun canSubscribeTo(blog: UserModel): Boolean =
        blog.blog && !blog.subscribed && (blog.id != id)

    fun canPin(story: StoryModel): Boolean =
        superUser || (story.user.id == id)

    fun canViewKpis(story: StoryModel): Boolean =
        superUser || (story.user.id == id)
}
