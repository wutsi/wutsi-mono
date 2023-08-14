package com.wutsi.tracking.manager.service.aggregator.source

import com.wutsi.tracking.manager.entity.SourceEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlySourceMapper : Mapper<SourceEntity, SourceKey, Long> {
    override fun map(read: SourceEntity): KeyPair<SourceKey, Long> =
        SourceValue(
            SourceKey(read.productId, read.source),
            read.totalReads,
        )
}
