package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType.LIKE_STORY_COMMAND
import com.wutsi.blog.event.EventType.UNLIKE_STORY_COMMAND
import com.wutsi.blog.like.dto.LikeStoryCommand
import com.wutsi.blog.like.dto.SearchLikeRequest
import com.wutsi.blog.like.dto.SearchLikeResponse
import com.wutsi.blog.like.dto.UnlikeStoryCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class LikeBackend(
    private val rest: RestTemplate,
    private val eventStream: EventStream,
) {
    @Value("\${wutsi.application.backend.like.endpoint}")
    private lateinit var endpoint: String

    fun execute(cmd: LikeStoryCommand) {
        eventStream.publish(LIKE_STORY_COMMAND, cmd)
    }

    fun execute(cmd: UnlikeStoryCommand) {
        eventStream.publish(UNLIKE_STORY_COMMAND, cmd)
    }

    fun search(request: SearchLikeRequest): SearchLikeResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchLikeResponse::class.java).body!!
}
