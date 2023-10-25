package com.wutsi.tracking.manager.service.aggregator.reader

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailyReaderMapper : Mapper<TrackEntity, ReaderKey, Long> {
    override fun map(track: TrackEntity): List<KeyPair<ReaderKey, Long>> =
        listOf(
            ReaderValue(
                ReaderKey(track.accountId, track.deviceId, track.productId!!),
                1,
            ),
        )
}
