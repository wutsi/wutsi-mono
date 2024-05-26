package com.wutsi.blog.story.service

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest

interface StorySearchFilter {
    fun filter(request: SearchStoryRequest, stories: List<StoryEntity>): List<StoryEntity>
}
