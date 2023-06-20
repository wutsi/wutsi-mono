package com.wutsi.tracking.manager.service.aggregator.scrolls

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DailyScrollFilter(private val date: LocalDate) : Filter<TrackEntity> {
    companion object {
        const val EVENT = "scroll"
        const val PAGE = "page.read"
    }

    override fun accept(track: TrackEntity): Boolean =
        !track.bot &&
            track.event.equals(EVENT) &&
            track.page.equals(PAGE, true) &&
            !track.productId.isNullOrEmpty() &&
            !track.correlationId.isNullOrEmpty() &&
            isNumeric(track.value) &&
            Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate().equals(date)

    private fun isNumeric(value: String?): Boolean {
        try {
            if (!value.isNullOrEmpty()) {
                value.toLong()
                return true
            }
        } catch (ex: Exception) {
            // Ignore
        }
        return false
    }
}
