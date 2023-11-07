package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailyClickMapper : Mapper<TrackEntity, ClickKey, Long> {
    override fun map(track: TrackEntity): List<KeyPair<ClickKey, Long>> =
        listOf(
            ClickValue(
                ClickKey(track.productId!!),
                1,
            ),
        )
}
