package com.wutsi.blog.app.model

import com.wutsi.blog.story.dto.StoryAccess
import com.wutsi.blog.story.dto.StoryAccess.PUBLIC
import com.wutsi.blog.story.dto.StoryStatus
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
) {
    fun isPublic(): Boolean =
        access == PUBLIC

    fun isNotPublic(): Boolean =
        !isPublic()
}
