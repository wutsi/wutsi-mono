package com.wutsi.ml.personalize.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.ml.personalize.dto.SortStoryRequest
import com.wutsi.ml.personalize.dto.SortStoryResponse
import com.wutsi.ml.personalize.dto.Story
import com.wutsi.ml.personalize.service.PersonalizeV1Service
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
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
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SortStoryQuery::class.java)
    }

    @PostMapping("/v1/personalize/queries/sort")
    fun search(@RequestBody request: SortStoryRequest): SortStoryResponse {
        val key = cacheKey(request)
        var response = cacheGet(key)
        if (response == null) {
            val result = service.sort(request)
            response = SortStoryResponse(
                stories = result.map {
                    Story(
                        id = it.first,
                        score = it.second,
                    )
                },
            )
            cachePut(key, response)
            logger.add("cache_hit", false)
        } else {
            logger.add("cache_hit", true)
        }
        return response
    }

    private fun cacheKey(request: SortStoryRequest): String =
        DigestUtils.md5Hex(SortStoryQuery::class.java.name + objectMapper.writeValueAsString(request))

    private fun cacheGet(key: String): SortStoryResponse? =
        try {
            cache.get(key, SortStoryResponse::class.java)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to get from cache. key=$key", ex)
            null
        }

    private fun cachePut(key: String, response: SortStoryResponse) {
        try {
            cache.put(key, response)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to put into cache. key=$key", ex)
        }
    }
}
