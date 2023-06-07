package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class ReadMapper(private val date: LocalDate) : Mapper<String, Long> {
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

    override fun map(track: TrackEntity): KeyPair<String, Long>? =
        track.productId?.let {
            StoryRead(
                track.productId,
                1,
            )
        }
}
