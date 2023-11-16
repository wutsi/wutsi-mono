package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector

class DailyDurationMapper(private val detector: TrafficSourceDetector) :
    Mapper<TrackEntity, DurationKey, DurationData> {
    companion object {
        const val EMAIL_READ_TIME_MILLIS = 60L * 1000L
    }

    override fun map(track: TrackEntity): List<KeyPair<DurationKey, DurationData>> =
        listOfNotNull(
            DurationValue(
                DurationKey(track.correlationId!!, track.productId!!),
                DurationData(track.event!!, track.time),
            ),
            if (track.event == DailyDurationFilter.EVENT_START && detector.detect(track) == TrafficSource.EMAIL) {
                DurationValue(
                    DurationKey(track.correlationId, track.productId),
                    DurationData(track.event, track.time + EMAIL_READ_TIME_MILLIS),
                )
            } else {
                null
            },
        )
}
