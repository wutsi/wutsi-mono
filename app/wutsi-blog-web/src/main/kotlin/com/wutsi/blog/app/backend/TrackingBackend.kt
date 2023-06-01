package com.wutsi.blog.app.backend

import com.wutsi.platform.core.stream.EventStream
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.event.EventType.PUSH_TRACK_COMMAND
import org.springframework.stereotype.Service

@Service
class TrackingBackend(private val eventStream: EventStream) {
    fun push(request: PushTrackRequest) {
        eventStream.publish(PUSH_TRACK_COMMAND, request)
    }
}
