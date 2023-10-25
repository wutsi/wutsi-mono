package com.wutsi.tracking.manager.service.aggregator.from

import com.wutsi.tracking.manager.entity.FromEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyFromMapper : Mapper<FromEntity, FromKey, Long> {
    override fun map(reader: FromEntity): List<KeyPair<FromKey, Long>> =
        listOf(
            FromValue(
                FromKey(reader.from),
                reader.totalReads,
            ),
        )
}
