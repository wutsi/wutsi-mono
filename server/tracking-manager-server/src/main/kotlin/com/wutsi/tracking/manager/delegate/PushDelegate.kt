package com.wutsi.tracking.manager.delegate

import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.dto.PushTrackResponse
import com.wutsi.tracking.manager.service.TrackService
import org.springframework.stereotype.Service

@Service
public class PushDelegate(private val service: TrackService) {
    public fun invoke(request: PushTrackRequest): PushTrackResponse =
        service.track(request)
}
