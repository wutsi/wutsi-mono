package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class ReadFilter(private val date: LocalDate) : Filter {
    companion object {
        const val EVENT = "readstart"
        const val PAGE = "page.read"
    }

    override fun accept(track: TrackEntity): Boolean =
        !track.bot &&
            track.event.equals(EVENT) &&
            track.page.equals(PAGE, true) &&
            !track.productId.isNullOrEmpty() &&
            Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate().equals(date)
}
