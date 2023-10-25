package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

open class DailyDurationFilter(private val date: LocalDate) : Filter<TrackEntity> {
    companion object {
        const val EVENT_START = "readstart"
        const val EVENT_END = "readend"
        const val PAGE = "page.read"
    }

    private val tomorrow = date.plusDays(1)

    override fun accept(track: TrackEntity): Boolean {
        return (!track.bot) &&
            (track.event.equals(EVENT_START) || track.event.equals(EVENT_END)) &&
            track.page.equals(PAGE, true) &&
            !track.productId.isNullOrEmpty() &&
            !track.correlationId.isNullOrEmpty() &&
            acceptEvent(track)
    }

    private fun acceptEvent(track: TrackEntity): Boolean {
        val trackDate = Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate()
        return (track.event.equals(EVENT_START) && trackDate.equals(date)) ||
            (track.event.equals(EVENT_END) && (trackDate.equals(date) || trackDate.equals(tomorrow)))
    }
}
