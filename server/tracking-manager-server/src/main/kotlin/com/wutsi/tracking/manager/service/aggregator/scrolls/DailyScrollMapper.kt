package com.wutsi.tracking.manager.service.aggregator.scrolls

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailyScrollMapper : Mapper<TrackEntity, ScrollKey, Long> {
    override fun map(track: TrackEntity): KeyPair<ScrollKey, Long> =
        ScrollValue(
            ScrollKey("${track.correlationId}|${track.productId}"),
            track.value!!.toLong(),
        )
}
