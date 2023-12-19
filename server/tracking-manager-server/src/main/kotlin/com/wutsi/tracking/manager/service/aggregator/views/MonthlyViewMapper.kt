package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.entity.ViewEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyViewMapper : Mapper<ViewEntity, ViewKey, Long> {
    override fun map(read: ViewEntity): List<KeyPair<ViewKey, Long>> =
        listOf(
            ViewValue(
                ViewKey(read.productId),
                read.totalViews,
            )
        )
}
