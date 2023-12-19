package com.wutsi.tracking.manager.service.aggregator.views

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonthlyViewMapperTest {
    private val mapper = MonthlyViewMapper()

    private val read = Fixtures.createViewEntity(
        productId = "123",
        totalViews = 1000,
    )

    @Test
    fun map() {
        val result = mapper.map(read)[0]
        assertEquals(read.productId, result.key.productId)
        assertEquals(read.totalViews, result.value)
    }
}
