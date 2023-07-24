package com.wutsi.blog.story.dto

import com.wutsi.blog.story.dto.StoryAccess.PUBLIC
import java.util.Date

data class Story(
    val id: Long = -1,
    val userId: Long = -1,
    val title: String? = null,
    val tagline: String? = null,
    val summary: String? = null,
    val thumbnailUrl: String? = null,
    val sourceUrl: String? = null,
    val sourceSite: String? = null,
    val wordCount: Int = 0,
    val readingMinutes: Int = 0,
    val language: String? = null,
    var content: String? = null,
    val contentType: String? = null,
    val status: StoryStatus = StoryStatus.DRAFT,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val publishedDateTime: Date? = null,
    val tags: List<Tag> = emptyList(),
    val slug: String = "",
    val readabilityScore: Int = 0,
    val topic: Topic? = null,
    val scheduledPublishDateTime: Date? = null,
    val access: StoryAccess = PUBLIC,
    val video: Boolean = false,

    val pinned: Boolean = false,
    val likeCount: Long = 0,
    val liked: Boolean = false,
    val commentCount: Long = 0,
    val commented: Boolean = false,
    val shareCount: Long = 0,
    val shared: Boolean = false,
    val readCount: Long = 0,
    var subscriberReaderCount: Long = 0,
)
