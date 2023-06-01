package com.wutsi.tracking.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.stream.Event
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.service.TrackService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val service: TrackService,
    private val objectMapper: ObjectMapper,
) {
    @EventListener
    fun handle(event: Event) {
        if (event.type == EventType.PUSH_TRACK_COMMAND) {
            service.track(
                objectMapper.readValue(event.payload, PushTrackRequest::class.java),
            )
        }
    }
}
