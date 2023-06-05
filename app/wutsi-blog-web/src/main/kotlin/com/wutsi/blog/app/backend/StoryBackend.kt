package com.wutsi.blog.app.backend

import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.GetStoryResponse
import com.wutsi.blog.client.story.RecommendStoryRequest
import com.wutsi.blog.client.story.RecommendStoryResponse
import com.wutsi.blog.client.story.SaveStoryRequest
import com.wutsi.blog.client.story.SaveStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.ImportStoryResponse
import com.wutsi.blog.story.dto.PublishStoryCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class StoryBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.story.endpoint}")
    private lateinit var endpoint: String

    @Value("\${wutsi.application.backend.story.endpoint2}")
    private lateinit var endpoint2: String

    fun create(request: SaveStoryRequest): SaveStoryResponse {
        return rest.postForEntity(endpoint, request, SaveStoryResponse::class.java).body!!
    }

    fun update(id: Long, request: SaveStoryRequest): SaveStoryResponse {
        return rest.postForEntity("$endpoint/$id", request, SaveStoryResponse::class.java).body!!
    }

    fun get(id: Long): GetStoryResponse {
        return rest.getForEntity("$endpoint/$id", GetStoryResponse::class.java).body!!
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

    fun publish(id: Long, request: PublishStoryCommand) {
        rest.postForEntity("$endpoint2/$id/commands/publish", request, Any::class.java)
    }

    fun import(command: ImportStoryCommand): ImportStoryResponse {
        return rest.postForEntity("$endpoint2/commands/import", command, ImportStoryResponse::class.java).body!!
    }

    fun recommend(request: RecommendStoryRequest): RecommendStoryResponse {
        return rest.postForEntity("$endpoint/recommend", request, RecommendStoryResponse::class.java).body!!
    }
}
