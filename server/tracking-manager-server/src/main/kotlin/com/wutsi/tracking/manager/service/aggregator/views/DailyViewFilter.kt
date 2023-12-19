package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.Filter
import com.wutsi.tracking.manager.util.EmailUtil
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

open class DailyViewFilter(private val date: LocalDate) : Filter<TrackEntity> {
    companion object {
        const val EVENT = "productview"
        const val PAGE = "page.product"
    }

    override fun accept(track: TrackEntity): Boolean =
        (!track.bot || EmailUtil.isImageProxy(track)) &&
            track.event.equals(EVENT) &&
            track.page.equals(PAGE, true) &&
            !track.productId.isNullOrEmpty() &&
            Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate().equals(date)
}
