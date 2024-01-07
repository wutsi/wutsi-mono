package com.wutsi.blog.story.service

import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.service.recommendation.StoryRecommenderFallbackStrategy
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class StoryRecommendationService(
    private val fallback: StoryRecommenderFallbackStrategy,
    private val logger: KVLogger,
) {
    fun recommend(request: RecommendStoryRequest): List<Long> {
        logger.add("request_device_id", request.deviceId)
        logger.add("request_reader_id", request.readerId)
        logger.add("request_limit", request.limit)

        return fallback.recommend(request)
    }
}
