package com.wutsi.blog.story.dto

import com.wutsi.blog.SortOrder
import java.util.Date

data class SearchStoryRequest(
    val storyIds: List<Long> = emptyList(),
    val userIds: List<Long> = emptyList(),
    val status: StoryStatus? = null,
    val topicId: Long? = null,
    val publishedStartDate: Date? = null,
    val publishedEndDate: Date? = null,
    val scheduledPublishedStartDate: Date? = null,
    val scheduledPublishedEndDate: Date? = null,
    val language: String? = null,
    val limit: Int = 20,
    val offset: Int = 0,
    val sortBy: StorySortStrategy = StorySortStrategy.RECOMMENDED,
    val sortOrder: SortOrder = SortOrder.DESCENDING,
    val dedupUser: Boolean = false,
    val tags: List<String> = emptyList(),
)
