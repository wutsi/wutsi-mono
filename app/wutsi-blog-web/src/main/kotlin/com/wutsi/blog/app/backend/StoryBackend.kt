package com.wutsi.blog.app.backend

import com.wutsi.blog.client.story.CountStoryResponse
import com.wutsi.blog.client.story.GetStoryReadabilityResponse
import com.wutsi.blog.client.story.RecommendStoryRequest
import com.wutsi.blog.client.story.RecommendStoryResponse
import com.wutsi.blog.client.story.SearchStoryRequest
import com.wutsi.blog.client.story.SearchStoryResponse
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.ImportStoryResponse
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.UpdateStoryCommand
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class StoryBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.story.endpoint}")
    private lateinit var endpoint: String

    @Value("\${wutsi.application.backend.story.endpoint2}")
    private lateinit var endpoint2: String

    fun create(command: CreateStoryCommand): CreateStoryResponse {
        return rest.postForEntity("$endpoint2/commands/create", command, CreateStoryResponse::class.java).body!!
    }

    fun update(command: UpdateStoryCommand) {
        rest.postForEntity("$endpoint2/commands/update", command, Any::class.java)
    }

    fun delete(command: DeleteStoryCommand) {
        rest.postForEntity("$endpoint2/commands/delete", command, Any::class.java)
    }

    fun get(id: Long): GetStoryResponse {
        return rest.getForEntity("$endpoint/$id", GetStoryResponse::class.java).body!!
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
        rest.postForEntity("$endpoint2/commands/publish", request, Any::class.java)
    }

    fun import(command: ImportStoryCommand): ImportStoryResponse {
        return rest.postForEntity("$endpoint2/commands/import", command, ImportStoryResponse::class.java).body!!
    }

    fun recommend(request: RecommendStoryRequest): RecommendStoryResponse {
        return rest.postForEntity("$endpoint/recommend", request, RecommendStoryResponse::class.java).body!!
    }
}
