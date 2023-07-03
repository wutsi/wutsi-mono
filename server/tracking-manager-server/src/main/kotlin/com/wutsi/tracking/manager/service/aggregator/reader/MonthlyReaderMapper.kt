package com.wutsi.tracking.manager.service.aggregator.reader

import com.wutsi.tracking.manager.entity.ReaderEntity
import com.wutsi.tracking.manager.service.aggregator.KeyPair
import com.wutsi.tracking.manager.service.aggregator.Mapper

open class MonthlyReaderMapper : Mapper<ReaderEntity, ReaderKey, Long> {
    override fun map(reader: ReaderEntity): KeyPair<ReaderKey, Long> =
        ReaderValue(
            ReaderKey(reader.accountId, reader.deviceId, reader.productId),
            reader.totalReads,
        )
}
