package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import com.wutsi.tracking.manager.util.EmailUtil
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

open class DailyDurationFilter(private val date: LocalDate) : Filter<TrackEntity> {
    companion object {
        const val EVENT_START = "readstart"
        const val EVENT_END = "readend"
        const val EVENT_SCROLL = "scroll"
        const val EVENT_CLICK = "click"
        const val PAGE = "page.read"
    }

    private val tomorrow = date.plusDays(1)
    private val events = listOf(
        EVENT_START,
        EVENT_END,
        EVENT_SCROLL,
        EVENT_CLICK
    )

    override fun accept(track: TrackEntity): Boolean {
        return (!track.bot || EmailUtil.isImageProxy(track)) &&
            events.contains(track.event) &&
            track.page.equals(PAGE, true) &&
            !track.productId.isNullOrEmpty() &&
            !track.correlationId.isNullOrEmpty() &&
            acceptEvent(track)
    }

    private fun acceptEvent(track: TrackEntity): Boolean {
        val trackDate = Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate()
        return trackDate.equals(date) || (track.event.equals(EVENT_END) && trackDate.equals(tomorrow))
    }
}
