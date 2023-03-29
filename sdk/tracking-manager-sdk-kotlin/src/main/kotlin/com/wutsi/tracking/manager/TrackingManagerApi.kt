package com.wutsi.tracking.manager

import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.dto.PushTrackResponse
import feign.Headers
import feign.RequestLine

public interface TrackingManagerApi {
    @RequestLine("POST /v1/tracks")
    @Headers(value = ["Content-Type: application/json"])
    public fun push(request: PushTrackRequest): PushTrackResponse
}
