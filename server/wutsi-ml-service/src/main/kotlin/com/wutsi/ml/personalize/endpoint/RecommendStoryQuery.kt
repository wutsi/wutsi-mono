package com.wutsi.ml.personalize.endpoint

import com.wutsi.ml.personalize.dto.RecommendStoryRequest
import com.wutsi.ml.personalize.dto.RecommendStoryResponse
import com.wutsi.ml.personalize.dto.Story
import com.wutsi.ml.personalize.service.PersonalizeV1Service
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.cache.Cache
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class RecommendStoryQuery(
    private val service: PersonalizeV1Service,
    private val cache: Cache,
    private val logger: KVLogger,
) {
    @PostMapping("/v1/personalize/queries/recommend")
    fun recommend(@RequestBody request: RecommendStoryRequest): RecommendStoryResponse {
        val cacheKey = key(request)
        var response = cache.get(cacheKey, RecommendStoryResponse::class.java)
        if (response == null) {
            logger.add("cache_hit", false)
            val result = service.recommend(request)
            response = RecommendStoryResponse(
                stories = result.map {
                    Story(
                        id = it.first,
                        score = it.second,
                    )
                },
            )

            cache.put(cacheKey, response)
        } else {
            logger.add("cache_hit", true)
        }
        return response
    }

    private fun key(request: RecommendStoryRequest): String {
        val data = request.userId.toString() + "-" +
            request.limit.toString()
        return DigestUtils.md5Hex(data)
    }
}
