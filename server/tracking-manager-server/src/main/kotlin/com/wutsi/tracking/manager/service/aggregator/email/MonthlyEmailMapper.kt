package com.wutsi.tracking.manager.service.aggregator.email

import com.wutsi.tracking.manager.entity.EmailEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyEmailMapper : Mapper<EmailEntity, EmailKey, Long> {
    override fun map(read: EmailEntity): List<KeyPair<EmailKey, Long>> =
        listOf(
            EmailValue(
                EmailKey(read.accountId, read.productId),
                read.totalReads,
            ),
        )
}
