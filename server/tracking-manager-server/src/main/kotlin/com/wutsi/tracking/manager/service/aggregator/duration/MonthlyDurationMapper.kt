package com.wutsi.tracking.manager.service.aggregator.duration

import com.wutsi.tracking.manager.entity.DurationEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyDurationMapper : Mapper<DurationEntity, DurationKey, DurationData> {
    override fun map(reader: DurationEntity): List<KeyPair<DurationKey, DurationData>> =
        listOf(
            DurationValue(
                DurationKey("-", reader.productId),
                DurationData("-", reader.totalMinutes),
            )
        )
}
