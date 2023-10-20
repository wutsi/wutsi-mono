package com.wutsi.tracking.manager.service.pipeline.filter

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector
import com.wutsi.tracking.manager.service.pipeline.Filter

class SourceFilter(private val detector: TrafficSourceDetector) : Filter {
    override fun filter(track: TrackEntity): TrackEntity {
        return track.copy(source = detector.detect(track).name)
    }
}
