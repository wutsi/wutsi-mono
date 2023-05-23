package com.wutsi.blog.app.page.story.model

import com.wutsi.blog.app.page.settings.model.UserModel
import com.wutsi.blog.client.story.StoryAccess
import com.wutsi.blog.client.story.StoryAccess.PUBLIC
import com.wutsi.blog.client.story.StoryStatus
import com.wutsi.blog.client.story.WPPStatus
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
    val status: StoryStatus = StoryStatus.draft,
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
    val topic: TopicModel = TopicModel(),
    val live: Boolean = false,
    val liveDateTime: String = "",
    val wppStatus: WPPStatus? = null,
    val socialMediaMessage: String? = null,
    val pinned: Boolean = false,
    val pinId: Long = -1,
    val scheduledPublishDateTime: String? = null,
    val scheduledPublishDateTimeAsDate: Date? = null,
    val publishToSocialMedia: Boolean = false,
    val access: StoryAccess = PUBLIC,
    var impressions: String = "",
) {
    fun isPublic(): Boolean =
        access == PUBLIC

    fun isNotPublic(): Boolean =
        !isPublic()
}
