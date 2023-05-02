package com.wutsi.blog.story.service.sort.algo

import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.story.service.sort.SortAlgorithm
import org.springframework.stereotype.Service

@Service("NoSortAlgorithm")
class NoSortAlgorithm : SortAlgorithm {
    override fun sort(request: SortStoryRequest): List<Long> {
        return request.storyIds
    }
}
