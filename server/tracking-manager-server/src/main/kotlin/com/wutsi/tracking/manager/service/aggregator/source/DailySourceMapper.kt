package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector

class DailySourceMapper(private val detector: TrafficSourceDetector) : Mapper<TrackEntity, SourceKey, Long> {
    override fun map(track: TrackEntity): KeyPair<SourceKey, Long> =
        SourceValue(
            SourceKey(track.productId!!, getSource(track)),
            1,
        )

    private fun getSource(track: TrackEntity) = detector.detect(track)
}
