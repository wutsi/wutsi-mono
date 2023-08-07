package com.wutsi.tracking.manager.service

import com.wutsi.tracking.manager.dto.PushTrackRequest
import com.wutsi.tracking.manager.dto.PushTrackResponse
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.pipeline.Pipeline
import org.springframework.stereotype.Service
import java.util.UUID

@Service
public class TrackService(
    private val pipeline: Pipeline,
) {
    fun track(request: PushTrackRequest): PushTrackResponse {
        pipeline.filter(
            track = TrackEntity(
                time = request.time,
                correlationId = request.correlationId,
                deviceId = request.deviceId,
                accountId = request.accountId,
                merchantId = request.merchantId,
                productId = request.productId,
                ua = request.ua,
                ip = request.ip,
                lat = request.lat,
                long = request.long,
                referrer = request.referrer,
                page = request.page,
                event = request.event,
                value = request.value,
                revenue = request.revenue,
                url = request.url,
                businessId = request.businessId,
                country = request.country,
            ),
        )

        return PushTrackResponse(
            transactionId = UUID.randomUUID().toString(),
        )
    }
}
