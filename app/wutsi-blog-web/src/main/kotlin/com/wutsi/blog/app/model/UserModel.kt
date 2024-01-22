package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.DurationUtils
import com.wutsi.blog.app.util.NumberUtils
import com.wutsi.blog.country.dto.Country
import com.wutsi.blog.story.dto.WPPConfig
import org.apache.commons.lang3.time.DateUtils
import java.util.Date
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
    val blog: Boolean = false,
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
    val totalDurationSeconds: Long = 0,
    val wpp: Boolean = false,
    val creationDateTime: Date = Date(),
    val clickCount: Long = 0,
    val storeId: String? = null,
    val shopUrl: String? = null,
    val orderCount: Long = 0,
    val donationCount: Long = 0,
    val totalSales: Long = 0,
) {
    val subscriberCountText: String
        get() = NumberUtils.toHumanReadable(subscriberCount)

    val readCountText: String
        get() = NumberUtils.toHumanReadable(readCount)

    val canEnableMonetization: Boolean
        get() = blog &&
                walletId == null &&
                countrySupportsMonetization

    val canJoinWPP: Boolean
        get() = blog &&
                !wpp &&
                meetWPPStoryThreshold &&
                meetWPPSubscriberThreshold &&
                meetWPPAgeThreshold &&
                walletId != null

    val canCreateStore: Boolean
        get() = blog &&
                storeId == null &&
                walletId != null
    val meetWPPStoryThreshold: Boolean
        get() = publishStoryCount >= WPPConfig.MIN_STORY_COUNT

    val meetWPPSubscriberThreshold: Boolean
        get() = subscriberCount >= WPPConfig.MIN_SUBSCRIBER_COUNT

    val meetWPPAgeThreshold: Boolean
        get() = DateUtils.addMonths(Date(), -WPPConfig.MIN_AGE_MONTHS).after(creationDateTime)

    val countrySupportsMonetization: Boolean
        get() = (country != null && Country.all.find { it.code.equals(country, true) } != null)

    fun canSubscribeTo(blog: UserModel): Boolean =
        blog.blog && (blog.id != id) && !blog.subscribed

    fun canDonateTo(blog: UserModel): Boolean =
        blog.blog && (blog.id != id) && (blog.walletId != null)

    fun canPin(story: StoryModel): Boolean =
        superUser || (story.user.id == id)

    val clickRatePercent: String
        get() = if (readCount == 0L) {
            "0%"
        } else {
            val percent = (100 * clickCount).toDouble() / readCount.toDouble()
            String.format("%.3f", percent)
        }

    val totalDurationText: String
        get() = DurationUtils.toHumanReadable(totalDurationSeconds)

    val averageDurationText: String
        get() = if (readCount == 0L) {
            DurationUtils.toHumanReadable(0)
        } else {
            DurationUtils.toHumanReadable(totalDurationSeconds / readCount)
        }
}
