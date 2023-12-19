package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailyViewMapper : Mapper<TrackEntity, ViewKey, Long> {
    override fun map(track: TrackEntity): List<KeyPair<ViewKey, Long>> =
        listOf(
            ViewValue(
                ViewKey(track.productId!!),
                1,
            ),
        )
}
