package com.wutsi.ml.embedding.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.blog.similarity.dto.SearchSimilarityRequest
import com.wutsi.blog.similarity.dto.SearchSimilarityResponse
import com.wutsi.blog.similarity.dto.Similarity
import com.wutsi.ml.embedding.service.TfIdfSimilarityService
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
class SearchSimilarStoriesQuery(
    private val service: TfIdfSimilarityService,
    private val cache: Cache,
    private val objectMapper: ObjectMapper,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchSimilarStoriesQuery::class.java)
    }

    @PostMapping("/v1/similarities/queries/search")
    fun search(@RequestBody request: SearchSimilarityRequest): SearchSimilarityResponse {
        val key = cacheKey(request)
        var response = cacheGet(key)
        if (response == null) {
            val result = service.search(request)
            response = SearchSimilarityResponse(
                similarities = result.map {
                    Similarity(
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

    private fun cacheKey(request: SearchSimilarityRequest): String =
        DigestUtils.md5Hex(objectMapper.writeValueAsBytes(request))

    private fun cacheGet(key: String): SearchSimilarityResponse? =
        try {
            cache.get(key, SearchSimilarityResponse::class.java)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to get from cache. key=$key", ex)
            null
        }

    private fun cachePut(key: String, response: SearchSimilarityResponse) {
        try {
            cache.put(key, response)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to put into cache. key=$key", ex)
        }
    }

}
