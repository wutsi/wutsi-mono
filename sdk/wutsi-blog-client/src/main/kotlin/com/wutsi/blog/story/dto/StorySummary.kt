package com.wutsi.blog.story.dto

import com.wutsi.blog.story.dto.StoryAccess.PUBLIC
import java.util.Date

data class StorySummary(
    val id: Long = -1,
    val userId: Long = -1,
    val title: String? = null,
    val tagline: String? = null,
    val summary: String? = null,
    val thumbnailUrl: String? = null,
    val sourceUrl: String? = null,
    val wordCount: Int = 0,
    val readingMinutes: Int = 0,
    val language: String? = null,
    val status: StoryStatus = StoryStatus.DRAFT,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val publishedDateTime: Date? = null,
    val slug: String = "",
    val topicId: Long? = null,
    val scheduledPublishDateTime: Date? = null,
    val access: StoryAccess = PUBLIC,

    val pinned: Boolean = false,
    val totalLikes: Long = 0,
    val liked: Boolean = false,
    val totalComments: Long = 0,
    val commented: Boolean = false,
)
