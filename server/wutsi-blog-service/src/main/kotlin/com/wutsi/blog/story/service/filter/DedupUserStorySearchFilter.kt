package com.wutsi.blog.story.service.filter

import com.wutsi.blog.story.domain.StoryEntity
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.service.StorySearchFilter
import org.springframework.stereotype.Service

@Service
class DedupUserStorySearchFilter : StorySearchFilter {
    override fun filter(request: SearchStoryRequest, stories: List<StoryEntity>): List<StoryEntity> {
        if (!request.dedupUser) {
            return stories
        }

        val authorIds = mutableSetOf<Long>()
        return stories.filter { authorIds.add(it.userId) }
    }
}
