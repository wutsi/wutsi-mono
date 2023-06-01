package com.wutsi.tracking.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.core.stream.Event
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.service.TrackService
import org.junit.jupiter.api.Test

internal class EventHandlerTest {

    @Test
    fun handle() {
        // GIVEN
        val service: TrackService = mock()
        val objectMapper = ObjectMapper()
        val handler = EventHandler(service, objectMapper)

        // WHEN
        val request = PushTrackRequest(time = 555L)
        handler.handle(
            Event(
                type = EventType.PUSH_TRACK_COMMAND,
                payload = objectMapper.writeValueAsString(request),
            ),
        )

        // THEN
        verify(service).track(request)
    }
}
