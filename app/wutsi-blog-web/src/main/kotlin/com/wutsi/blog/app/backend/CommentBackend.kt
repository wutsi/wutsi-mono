package com.wutsi.blog.app.backend

import com.wutsi.blog.comment.dto.CommentStoryCommand
import com.wutsi.blog.comment.dto.SearchCommentRequest
import com.wutsi.blog.comment.dto.SearchCommentResponse
import com.wutsi.blog.event.EventType.COMMENT_STORY_COMMAND
import com.wutsi.platform.core.stream.EventStream
import org.apache.commons.text.StringEscapeUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CommentBackend(
    private val rest: RestTemplate,
    private val eventStream: EventStream,
) {
    @Value("\${wutsi.application.backend.comment.endpoint}")
    private lateinit var endpoint: String

    fun comment(cmd: CommentStoryCommand) {
        val dup = cmd.copy(text = StringEscapeUtils.escapeJson(cmd.text))
        eventStream.publish(COMMENT_STORY_COMMAND, dup)
    }

    fun search(request: SearchCommentRequest): SearchCommentResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchCommentResponse::class.java).body!!
}
