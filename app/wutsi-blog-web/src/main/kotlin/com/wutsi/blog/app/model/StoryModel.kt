package com.wutsi.blog.app.model

import com.wutsi.blog.app.util.DurationUtils
import com.wutsi.blog.app.util.NumberUtils
import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StoryAccess.PUBLIC
import com.wutsi.blog.story.dto.StoryStatus
import java.lang.Long.max
import java.util.Date

data class StoryModel(
    val id: Long = -1,
    val user: UserModel = UserModel(),
    val title: String = "",
    val summary: String = "",
    val tagline: String = "",
    val thumbnailImage: HtmlImageModel? = null,
    val thumbnailUrl: String? = null,
    val thumbnailLargeUrl: String? = null,
    val thumbnailLargeWidth: Int? = null,
    val thumbnailLargeHeight: Int? = null,
    val thumbnailSmallUrl: String? = null,
    val thumbnailSmallWidth: Int? = null,
    val thumbnailSmallHeight: Int? = null,
    val sourceUrl: String? = null,
    val sourceSite: String? = null,
    val wordCount: Int = 0,
    val readabilityScore: Int = 0,
    val readingMinutes: Int = 0,
    val language: String? = null,
    val content: String? = null,
    val contentType: String? = null,
    val status: StoryStatus = StoryStatus.DRAFT,
    val draft: Boolean = true,
    val published: Boolean = false,
    val creationDateTime: String = "",
    val modificationDateTime: String = "",
    val publishedDateTime: String = "",
    val modificationDateTimeAsDate: Date? = null,
    val publishedDateTimeAsDate: Date? = null,
    val creationDateTimeISO8601: String = "",
    val modificationDateTimeISO8601: String = "",
    val publishedDateTimeISO8601: String? = null,
    val tags: List<TagModel> = emptyList(),
    val slug: String = "",
    val url: String = "",
    val topic: TopicModel = TopicModel(),
    val pinned: Boolean = false,
    val scheduledPublishDateTime: String? = null,
    val scheduledPublishDateTimeAsDate: Date? = null,
    val access: StoryAccess = PUBLIC,
    val likeCount: Long = 0,
    val liked: Boolean = false,
    val commentCount: Long = 0,
    val commented: Boolean = false,
    val shareCount: Long = 0,
    val shared: Boolean = false,
    val readCount: Long = 0,
    val readEmailCount: Long = 0,
    val video: Boolean = false,
    var subscriberReaderCount: Long = 0,
    var recipientCount: Long = 0,
    val userSubscriberCount: Long = 0,
    val totalDurationSeconds: Long = 0,
    val wpp: Boolean = false,
    val clickCount: Long = 0,
) {
    companion object {
        const val OPEN_RATE_ADJUSTMENT = 1
    }

    val readCountText: String
        get() = NumberUtils.toHumanReadable(readCount)

    val likeCountText: String
        get() = NumberUtils.toHumanReadable(likeCount)

    val shareCountText: String
        get() = NumberUtils.toHumanReadable(shareCount)

    val commentCountText: String
        get() = NumberUtils.toHumanReadable(commentCount)

    val recipientCountText: String
        get() = NumberUtils.toHumanReadable(recipientCount)

    val subscriberReaderPercent: String
        get() = if (userSubscriberCount == 0L || subscriberReaderCount == 0L) {
            "0%"
        } else {
            val percent = max(1, 100L * subscriberReaderCount / userSubscriberCount)
            "$percent%"
        }

    val readEmailCountText: String
        get() = NumberUtils.toHumanReadable(readEmailCount)

    val openRatePercent: String
        get() = if (recipientCount == 0L || readEmailCount == 0L) {
            "0%"
        } else {
            val percent = max(1, 100L * readEmailCount / (recipientCount * OPEN_RATE_ADJUSTMENT))
            "$percent%"
        }

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

    fun isPublic(): Boolean =
        access == PUBLIC

    fun isNotPublic(): Boolean =
        !isPublic()
}
