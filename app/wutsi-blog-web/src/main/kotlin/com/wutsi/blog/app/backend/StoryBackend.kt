package com.wutsi.blog.app.backend

import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.GetStoryResponse
import com.wutsi.blog.client.story.ImportStoryRequest
import com.wutsi.blog.client.story.ImportStoryResponse
import com.wutsi.blog.client.story.PublishStoryRequest
import com.wutsi.blog.client.story.PublishStoryResponse
import com.wutsi.blog.client.story.RecommendStoryRequest
import com.wutsi.blog.client.story.RecommendStoryResponse
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.client.story.SortStoryRequest
import com.wutsi.blog.client.story.SortStoryResponse
import com.wutsi.blog.client.story.TranslateStoryResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class StoryBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.story.endpoint}")
    private lateinit var endpoint: String

    fun create(request: SaveStoryRequest): SaveStoryResponse {
        return rest.postForEntity(endpoint, request, SaveStoryResponse::class.java).body!!
    }

    fun update(id: Long, request: SaveStoryRequest): SaveStoryResponse {
        return rest.postForEntity("$endpoint/$id", request, SaveStoryResponse::class.java).body!!
    }

    fun get(id: Long): GetStoryResponse {
        return rest.getForEntity("$endpoint/$id", GetStoryResponse::class.java).body!!
    }

    fun translate(id: Long, language: String): TranslateStoryResponse {
        return rest.getForEntity(
            "$endpoint/$id/translate?language=$language",
            TranslateStoryResponse::class.java,
        ).body!!
    }

    fun delete(id: Long) {
        rest.delete("$endpoint/$id")
    }

    fun readability(id: Long): GetStoryReadabilityResponse {
        return rest.getForEntity("$endpoint/$id/readability", GetStoryReadabilityResponse::class.java).body!!
    }

    fun search(request: SearchStoryRequest): SearchStoryResponse {
        return rest.postForEntity("$endpoint/search", request, SearchStoryResponse::class.java).body!!
    }

    fun count(request: SearchStoryRequest): CountStoryResponse {
        return rest.postForEntity("$endpoint/count", request, CountStoryResponse::class.java).body!!
    }

    fun publish(id: Long, request: PublishStoryRequest): PublishStoryResponse {
        return rest.postForEntity("$endpoint/$id/publish", request, PublishStoryResponse::class.java).body!!
    }

    fun import(request: ImportStoryRequest): ImportStoryResponse {
        return rest.postForEntity("$endpoint/import", request, ImportStoryResponse::class.java).body!!
    }

    fun sort(request: SortStoryRequest): SortStoryResponse {
        return rest.postForEntity("$endpoint/sort", request, SortStoryResponse::class.java).body!!
    }

    fun recommend(request: RecommendStoryRequest): RecommendStoryResponse {
        return rest.postForEntity("$endpoint/recommend", request, RecommendStoryResponse::class.java).body!!
    }
}
