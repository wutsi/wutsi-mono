package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import com.wutsi.tracking.manager.util.EmailBotUtil
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

open class DailyReadFilter(private val date: LocalDate) : Filter<TrackEntity> {
    companion object {
        const val EVENT = "readstart"
        const val PAGE = "page.read"
    }

    override fun accept(track: TrackEntity): Boolean =
        (!track.bot || EmailBotUtil.isBot(track)) &&
            track.event.equals(EVENT) &&
            track.page.equals(PAGE, true) &&
            !track.productId.isNullOrEmpty() &&
            Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate().equals(date)
}
