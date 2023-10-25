package com.wutsi.tracking.manager.service.aggregator.reads

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test

internal class MonthlyReadMapperTest {
    private val mapper = MonthlyReadMapper()

    private val read = Fixtures.createReadEntity(
        productId = "123",
        totalReads = 1000,
    )

    @Test
    fun map() {
        val result = mapper.map(read)[0]
        kotlin.test.assertEquals(read.productId, result.key.productId)
        kotlin.test.assertEquals(read.totalReads, result.value)
    }
}
