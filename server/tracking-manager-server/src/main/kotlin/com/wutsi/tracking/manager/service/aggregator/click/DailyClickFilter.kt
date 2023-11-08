package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

open class DailyClickFilter(private val date: LocalDate) : Filter<TrackEntity> {
    companion object {
        const val EVENT = "click"
        const val PAGE = "page.read"
    }

    override fun accept(track: TrackEntity): Boolean =
        !track.bot &&
            track.event.equals(EVENT) &&
            track.page.equals(PAGE, true) &&
            !track.productId.isNullOrEmpty() &&
            !track.deviceId.isNullOrEmpty() &&
            Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate().equals(date)
}
