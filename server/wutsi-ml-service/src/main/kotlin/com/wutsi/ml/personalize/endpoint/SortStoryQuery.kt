package com.wutsi.ml.personalize.endpoint

import com.wutsi.ml.personalize.dto.SortStoryRequest
import com.wutsi.ml.personalize.dto.SortStoryResponse
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
class SortStoryQuery(
    private val service: PersonalizeV1Service,
    private val cache: Cache,
    private val logger: KVLogger,
) {
    @PostMapping("/v1/personalize/queries/sort")
    fun sort(@RequestBody request: SortStoryRequest): SortStoryResponse {
        val cacheKey = key(request)
        var response = cache.get(cacheKey, SortStoryResponse::class.java)
        if (response == null) {
            logger.add("cache_hit", false)
            val result = service.sort(request)
            response = SortStoryResponse(
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

    private fun key(request: SortStoryRequest): String {
        val data = request.userId.toString() + "-" +
            request.storyIds.sorted().joinToString(",")
        return DigestUtils.md5Hex(data)
    }
}
