package com.wutsi.tracking.manager.service.aggregator.readers

import com.wutsi.tracking.manager.Fixtures
import com.wutsi.tracking.manager.service.aggregator.reader.MonthlyReaderMapper
import org.junit.jupiter.api.Test

internal class MonthlyReaderMapperTest {
    private val mapper = MonthlyReaderMapper()

    private val read = Fixtures.createReaderEntity(
        accountId = "1",
        deviceId = "device-id-1",
        productId = "123",
        totalReads = 1000,
    )

    @Test
    fun map() {
        val result = mapper.map(read)[0]
        kotlin.test.assertEquals(read.accountId, result.key.accountId)
        kotlin.test.assertEquals(read.deviceId, result.key.deviceId)
        kotlin.test.assertEquals(read.productId, result.key.productId)
        kotlin.test.assertEquals(read.totalReads, result.value)
    }
}
