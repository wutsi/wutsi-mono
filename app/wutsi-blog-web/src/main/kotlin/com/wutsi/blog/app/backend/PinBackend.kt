package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.event.EventType.UNPIN_STORY_COMMAND
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.SearchPinRequest
import com.wutsi.blog.pin.dto.SearchPinResponse
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class PinBackend(
    private val rest: RestTemplate,
    private val eventStream: EventStream,
) {
    @Value("\${wutsi.application.backend.pin.endpoint}")
    private lateinit var endpoint: String

    fun execute(cmd: PinStoryCommand) {
        eventStream.publish(PIN_STORY_COMMAND, cmd)
    }

    fun execute(cmd: UnpinStoryCommand) {
        eventStream.publish(UNPIN_STORY_COMMAND, cmd)
    }

    fun search(request: SearchPinRequest): SearchPinResponse =
        rest.postForEntity("$endpoint/queries/search", request, SearchPinResponse::class.java).body!!
}
