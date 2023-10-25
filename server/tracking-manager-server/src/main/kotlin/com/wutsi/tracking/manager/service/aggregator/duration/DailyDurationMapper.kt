package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailyDurationMapper : Mapper<TrackEntity, DurationKey, Long> {
    override fun map(track: TrackEntity): List<KeyPair<DurationKey, Long>> =
        listOf(
            DurationValue(
                DurationKey(track.correlationId!!, track.productId!!),
                track.time,
            ),
        )
}
