package com.wutsi.blog.story.service

import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.service.similarity.StorySimilarityFallbackStrategy
import com.wutsi.blog.story.service.similarity.StorySimilarityMLStrategy
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class StorySimilarityService(
    private val algorithm: StorySimilarityMLStrategy,
    private val fallback: StorySimilarityFallbackStrategy,
    private val logger: KVLogger,
) {
    fun search(request: SearchSimilarStoryRequest): List<Long> {
        logger.add("request_story_ids", request.storyIds)
        logger.add("request_limit", request.limit)

        return algorithm.search(request)
            .ifEmpty {
                fallback.search(request)
            }
    }
}
