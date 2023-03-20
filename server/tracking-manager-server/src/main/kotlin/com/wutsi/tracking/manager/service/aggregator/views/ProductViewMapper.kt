package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class ProductViewMapper(private val date: LocalDate) : Mapper<ProductKey, Long> {
    companion object {
        const val EVENT = "load"
        const val PAGE = "page.web.product"
    }

    override fun accept(track: TrackEntity): Boolean =
        !track.bot &&
            track.event.equals(EVENT) &&
            track.page.equals(PAGE, true) &&
            !track.productId.isNullOrEmpty() &&
            Instant.ofEpochMilli(track.time).atZone(ZoneOffset.UTC).toLocalDate().equals(date)

    override fun map(track: TrackEntity): KeyPair<ProductKey, Long>? =
        track.productId?.let {
            ProductView(
                ProductKey(track.businessId ?: "-1", track.productId),
                1,
            )
        }
}
