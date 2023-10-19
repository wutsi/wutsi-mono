package com.wutsi.tracking.manager.service.aggregator.email

import com.wutsi.tracking.manager.entity.TrackEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

class DailyEmailMapper : Mapper<TrackEntity, EmailKey, Long> {
    override fun map(track: TrackEntity): KeyPair<EmailKey, Long> =
        EmailValue(
            EmailKey(track.accountId!!, track.productId!!),
            1,
        )
}
