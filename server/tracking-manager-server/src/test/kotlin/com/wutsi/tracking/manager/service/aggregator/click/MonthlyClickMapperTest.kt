package com.wutsi.tracking.manager.service.aggregator.click

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test

class MonthlyClickMapperTest {
    private val mapper = MonthlyClickMapper()

    private val read = Fixtures.createClickEntity(
        productId = "123",
        totalClicks = 1000,
    )

    @Test
    fun map() {
        val result = mapper.map(read)[0]
        kotlin.test.assertEquals(read.productId, result.key.productId)
        kotlin.test.assertEquals(read.totalClicks, result.value)
    }
}
