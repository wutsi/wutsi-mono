package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailyReadMapper : Mapper<TrackEntity, ReadKey, Long> {
    override fun map(track: TrackEntity): List<KeyPair<ReadKey, Long>> =
        listOf(
            ReadValue(
                ReadKey(track.productId!!),
                1,
            ),
        )
}
