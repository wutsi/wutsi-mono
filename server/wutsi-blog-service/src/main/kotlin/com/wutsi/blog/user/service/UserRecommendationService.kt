package com.wutsi.blog.user.service

import com.wutsi.blog.user.dto.RecommendUserRequest
import com.wutsi.blog.user.service.recommendation.UserRecommenderFallbackStrategy
import com.wutsi.blog.user.service.recommendation.UserRecommenderMLStrategy
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class UserRecommendationService(
    private val algorithm: UserRecommenderMLStrategy,
    private val fallback: UserRecommenderFallbackStrategy,
    private val logger: KVLogger,
) {
    fun recommend(request: RecommendUserRequest): List<Long> {
        logger.add("request_device_id", request.deviceId)
        logger.add("request_reader_id", request.readerId)
        logger.add("request_limit", request.limit)

        return algorithm.recommend(request)
            .ifEmpty {
                fallback.recommend(request)
            }
    }
}
