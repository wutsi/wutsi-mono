package com.wutsi.tracking.manager.service.aggregator.scrolls

import com.wutsi.tracking.manager.entity.ScrollEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyScrollMapper : Mapper<ScrollEntity, ScrollKey, Long> {
    override fun map(read: ScrollEntity): KeyPair<ScrollKey, Long> =
        ScrollValue(
            ScrollKey(read.productId),
            read.averageScroll,
        )
}
