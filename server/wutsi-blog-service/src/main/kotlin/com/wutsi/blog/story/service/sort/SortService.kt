package com.wutsi.blog.story.service.sort

import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.client.story.SortStoryResponse
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class SortService(
    private val algos: SortAlgorithmFactory,
    private val logger: KVLogger,
) {
    fun sort(request: SortStoryRequest): SortStoryResponse {
        log(request)
        return SortStoryResponse(storyIds = algos.get(request.algorithm).sort(request))
    }

    private fun log(request: SortStoryRequest) {
        logger.add("StoryIds", request.storyIds)
        logger.add("UserId", request.userId)
        logger.add("SortAlgorithm", request.algorithm)
    }
}
