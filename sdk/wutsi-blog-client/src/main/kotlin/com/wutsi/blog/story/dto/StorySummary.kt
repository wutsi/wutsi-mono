package com.wutsi.blog.story.dto

import com.wutsi.blog.story.dto.StoryAccess.PUBLIC
import java.util.Date

data class StorySummary(
    val id: Long = -1,
    val userId: Long = -1,
    val title: String? = null,
    val summary: String? = null,
    val thumbnailUrl: String? = null,
    val wordCount: Int = 0,
    val readingMinutes: Int = 0,
    val language: String? = null,
    val status: StoryStatus = StoryStatus.DRAFT,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val publishedDateTime: Date? = null,
    val contentModificationDateTime: Date = Date(),
    val slug: String = "",
    val topicId: Long? = null,
    val scheduledPublishDateTime: Date? = null,
    val access: StoryAccess = PUBLIC,
    val pinned: Boolean = false,
    val likeCount: Long = 0,
    val liked: Boolean = false,
    val video: Boolean = false,
    val commentCount: Long = 0,
    val commented: Boolean = false,
    val shareCount: Long = 0,
    val shared: Boolean = false,
    val readCount: Long = 0,
    val recipientCount: Long = 0,
    val readerCount: Long = 0,
    val subscriberReaderCount: Long = 0,
    val emailReaderCount: Long = 0,
    val clickCount: Long = 0,
    val totalDurationSeconds: Long = 0,
    val productId: Long? = null,
    val categoryId: Long? = null,
)
