package com.wutsi.tracking.manager.endpoint

import com.wutsi.tracking.manager.`delegate`.PushDelegate
import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.dto.PushTrackResponse
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class PushController(
    public val `delegate`: PushDelegate,
) {
    @PostMapping("/v1/tracks")
    public fun invoke(
        @Valid @RequestBody
        request: PushTrackRequest,
    ): PushTrackResponse =
        delegate.invoke(request)
}
