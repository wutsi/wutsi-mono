package com.wutsi.tracking.manager.service.aggregator.reader

import com.wutsi.tracking.manager.entity.ReaderEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyReaderMapper : Mapper<ReaderEntity, ReaderKey, Long> {
    override fun map(reader: ReaderEntity): List<KeyPair<ReaderKey, Long>> =
        listOf(
            ReaderValue(
                ReaderKey(reader.accountId, reader.deviceId, reader.productId),
                reader.totalReads,
            ),
        )
}
