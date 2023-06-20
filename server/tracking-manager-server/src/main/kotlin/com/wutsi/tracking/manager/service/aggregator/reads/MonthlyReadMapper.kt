package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.entity.ReadEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyReadMapper : Mapper<ReadEntity, ReadKey, Long> {
    override fun map(read: ReadEntity): KeyPair<ReadKey, Long> =
        ReadValue(
            ReadKey(read.productId),
            read.totalReads,
        )
}
