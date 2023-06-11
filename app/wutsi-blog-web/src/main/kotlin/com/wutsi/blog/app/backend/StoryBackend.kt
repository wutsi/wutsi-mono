package com.wutsi.blog.app.backend

import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.ImportStoryResponse
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.UnpublishStoryCommand
import com.wutsi.blog.story.dto.UpdateStoryCommand
import com.wutsi.blog.subscription.dto.GetStoryReadabilityResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class StoryBackend(private val rest: RestTemplate) {
    @Value("\${wutsi.application.backend.story.endpoint}")
    private lateinit var endpoint: String

    fun create(command: CreateStoryCommand): CreateStoryResponse {
        return rest.postForEntity("$endpoint/commands/create", command, CreateStoryResponse::class.java).body!!
    }

    fun update(command: UpdateStoryCommand) {
        rest.postForEntity("$endpoint/commands/update", command, Any::class.java)
    }

    fun delete(command: DeleteStoryCommand) {
        rest.postForEntity("$endpoint/commands/delete", command, Any::class.java)
    }

    fun get(id: Long): GetStoryResponse {
        return rest.getForEntity("$endpoint/$id", GetStoryResponse::class.java).body!!
    }

    fun readability(id: Long): GetStoryReadabilityResponse {
        return rest.getForEntity("$endpoint/$id/readability", GetStoryReadabilityResponse::class.java).body!!
    }

    fun search(request: SearchStoryRequest): SearchStoryResponse {
        return rest.postForEntity("$endpoint/queries/search", request, SearchStoryResponse::class.java).body!!
    }

    fun publish(request: PublishStoryCommand) {
        rest.postForEntity("$endpoint/commands/publish", request, Any::class.java)
    }

    fun unpublish(request: UnpublishStoryCommand) {
        rest.postForEntity("$endpoint/commands/unpublish", request, Any::class.java)
    }

    fun import(command: ImportStoryCommand): ImportStoryResponse {
        return rest.postForEntity("$endpoint/commands/import", command, ImportStoryResponse::class.java).body!!
    }
}
