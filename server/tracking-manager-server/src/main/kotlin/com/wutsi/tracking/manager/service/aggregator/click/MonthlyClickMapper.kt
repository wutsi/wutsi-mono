package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.tracking.manager.entity.ClickEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyClickMapper : Mapper<ClickEntity, ClickKey, Long> {
    override fun map(click: ClickEntity): List<KeyPair<ClickKey, Long>> =
        listOf(
            ClickValue(
                ClickKey(click.productId),
                click.totalClicks,
            )
        )
}
