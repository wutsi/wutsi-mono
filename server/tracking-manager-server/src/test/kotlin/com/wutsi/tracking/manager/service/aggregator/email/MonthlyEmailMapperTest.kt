package com.wutsi.tracking.manager.service.aggregator.email

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonthlyEmailMapperTest {
    private val mapper = MonthlyEmailMapper()

    private val entity = Fixtures.createEmailEntity(
        accountId = "111",
        productId = "123",
        totalReads = 1000,
    )

    @Test
    fun map() {
        val result = mapper.map(entity)[0]
        assertEquals(entity.accountId, result.key.accountId)
        assertEquals(entity.productId, result.key.productId)
        assertEquals(entity.totalReads, result.value)
    }
}
