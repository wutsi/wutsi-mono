package com.wutsi.ml.similarity.endpoint

import com.wutsi.ml.similarity.dto.SearchSimilarityRequest
import com.wutsi.ml.similarity.dto.SearchSimilarityResponse
import com.wutsi.ml.similarity.model.SimilarityModelFactory
import com.wutsi.platform.core.logging.KVLogger
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.cache.Cache
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class SearchSimilarityQuery(
    private val factory: SimilarityModelFactory,
    private val cache: Cache,
    private val logger: KVLogger,
) {
    @PostMapping("/v1/similarities/queries/search")
    fun search(@RequestBody request: SearchSimilarityRequest): SearchSimilarityResponse {
        val cacheKey = key(request)
        var response = cache.get(cacheKey, SearchSimilarityResponse::class.java)
        if (response == null) {
            logger.add("cache_hit", false)
            response = factory.get(request.model).search(request)

            cache.put(cacheKey, response)
        } else {
            logger.add("cache_hit", true)
        }
        return response
    }

    private fun key(request: SearchSimilarityRequest): String {
        val data = request.model.name + "-" +
            request.limit + "-" +
            request.itemIds.sorted().joinToString(",")
        return DigestUtils.md5Hex(data)
    }
}
