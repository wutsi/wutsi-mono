package com.wutsi.tracking.manager.service.aggregator.email

import com.wutsi.blog.kpi.dto.TrafficSource
import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import com.wutsi.tracking.manager.service.aggregator.TrafficSourceDetector
import com.wutsi.tracking.manager.service.aggregator.reads.DailyReadFilter

open class DailyEmailFilter(
    private val dailyFilter: DailyReadFilter,
    private val detector: TrafficSourceDetector
) : Filter<TrackEntity> {
    override fun accept(track: TrackEntity): Boolean =
        !track.accountId.isNullOrEmpty() &&
            detector.detect(track) == TrafficSource.EMAIL &&
            dailyFilter.accept(track)
}
