package com.wutsi.blog.app.backend

import com.wutsi.blog.event.EventType.PIN_STORY_COMMAND
import com.wutsi.blog.event.EventType.UNPIN_STORY_COMMAND
import com.wutsi.blog.pin.dto.PinStoryCommand
import com.wutsi.blog.pin.dto.UnpinStoryCommand
import com.wutsi.platform.core.stream.EventStream
import org.springframework.stereotype.Service

@Service
class PinBackend(
    private val eventStream: EventStream,
) {
    fun pin(cmd: PinStoryCommand) {
        eventStream.publish(PIN_STORY_COMMAND, cmd)
    }

    fun unpin(cmd: UnpinStoryCommand) {
        eventStream.publish(UNPIN_STORY_COMMAND, cmd)
    }
}
