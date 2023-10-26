package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType.VIEW_STORY_COMMAND
import com.wutsi.blog.story.dto.CreateStoryCommand
import com.wutsi.blog.story.dto.CreateStoryResponse
import com.wutsi.blog.story.dto.DeleteStoryCommand
import com.wutsi.blog.story.dto.GetStoryReadabilityResponse
import com.wutsi.blog.story.dto.GetStoryResponse
import com.wutsi.blog.story.dto.ImportStoryCommand
import com.wutsi.blog.story.dto.ImportStoryResponse
import com.wutsi.blog.story.dto.PublishStoryCommand
import com.wutsi.blog.story.dto.RecommendStoryRequest
import com.wutsi.blog.story.dto.RecommendStoryResponse
import com.wutsi.blog.story.dto.SearchSimilarStoryRequest
import com.wutsi.blog.story.dto.SearchSimilarStoryResponse
import com.wutsi.blog.story.dto.SearchStoryRequest
import com.wutsi.blog.story.dto.SearchStoryResponse
import com.wutsi.blog.story.dto.UnpublishStoryCommand
import com.wutsi.blog.story.dto.UpdateStoryCommand
import com.wutsi.blog.story.dto.ValidateStoryWPPEligibilityResponse
import com.wutsi.blog.story.dto.ViewStoryCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class StoryBackend(
    private val rest: RestTemplate,
    private val eventStream: EventStream,
) {
    @Value("\${wutsi.application.backend.story.endpoint}")
    private lateinit var endpoint: String

    fun create(command: CreateStoryCommand): CreateStoryResponse =
        rest.postForEntity("$endpoint/commands/create", command, CreateStoryResponse::class.java).body!!

    fun update(command: UpdateStoryCommand) {
        rest.postForEntity("$endpoint/commands/update", command, Any::class.java)
    }

    fun delete(command: DeleteStoryCommand) {
        rest.postForEntity("$endpoint/commands/delete", command, Any::class.java)
    }

    fun get(id: Long): GetStoryResponse =
        rest.getForEntity("$endpoint/$id", GetStoryResponse::class.java).body!!

    fun readability(id: Long): GetStoryReadabilityResponse =
        rest.getForEntity(
            "$endpoint/queries/get-readability?story-id=$id",
            GetStoryReadabilityResponse::class.java,
        ).body!!

    fun validateWPPEligibility(id: Long): ValidateStoryWPPEligibilityResponse =
        rest.getForEntity(
            "$endpoint/queries/validate-wpp-eligibility?story-id=$id",
            ValidateStoryWPPEligibilityResponse::class.java,
        ).body!!

    fun search(request: SearchStoryRequest): SearchStoryResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchStoryResponse::class.java).body!!

    fun searchSimilar(request: SearchSimilarStoryRequest): SearchSimilarStoryResponse =
        rest.postForEntity("$endpoint/queries/search-similar", request, SearchSimilarStoryResponse::class.java).body!!

    fun recommend(request: RecommendStoryRequest): RecommendStoryResponse =
        rest.postForEntity("$endpoint/queries/recommend", request, RecommendStoryResponse::class.java).body!!

    fun publish(request: PublishStoryCommand) {
        rest.postForEntity("$endpoint/commands/publish", request, Any::class.java)
    }

    fun unpublish(request: UnpublishStoryCommand) {
        rest.postForEntity("$endpoint/commands/unpublish", request, Any::class.java)
    }

    fun import(command: ImportStoryCommand): ImportStoryResponse =
        rest.postForEntity("$endpoint/commands/import", command, ImportStoryResponse::class.java).body!!

    fun view(command: ViewStoryCommand) {
        eventStream.publish(VIEW_STORY_COMMAND, command)
    }
}
