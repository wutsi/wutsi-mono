package com.wutsi.tracking.manager.service.aggregator.scrolls

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test

internal class MonthlyScrollMapperTest {
    private val mapper = MonthlyScrollMapper()

    private val read = Fixtures.createScrollEntity(
        productId = "123",
        averageScroll = 1000,
    )

    @Test
    fun map() {
        val result = mapper.map(read)
        kotlin.test.assertEquals(read.productId, result.key.productId)
        kotlin.test.assertEquals(read.averageScroll, result.value)
    }
}
