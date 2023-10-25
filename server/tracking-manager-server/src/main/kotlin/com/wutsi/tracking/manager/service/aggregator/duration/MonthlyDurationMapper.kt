package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.entity.DurationEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyDurationMapper : Mapper<DurationEntity, DurationKey, Long> {
    override fun map(reader: DurationEntity): List<KeyPair<DurationKey, Long>> =
        listOf(
            DurationValue(
                DurationKey("-", reader.productId),
                reader.totalMinutes,
            )
        )
}
