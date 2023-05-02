package com.wutsi.blog.story.service.sort.algo

import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.story.service.ViewService
import com.wutsi.blog.story.service.sort.SortAlgorithm
import org.springframework.stereotype.Service

@Service("RecommendedSortAlgorithm")
class RecommendedSortAlgorithm(
    private val viewService: ViewService,
) : SortAlgorithm {
    override fun sort(request: SortStoryRequest): List<Long> {
        val viewed = request.storyIds.filter { viewService.contains(request.deviceId, it) }

        val result = mutableListOf<Long>()
        result.addAll(request.storyIds.filter { !viewed.contains(it) })
        result.addAll(viewed)
        return result
    }
}
