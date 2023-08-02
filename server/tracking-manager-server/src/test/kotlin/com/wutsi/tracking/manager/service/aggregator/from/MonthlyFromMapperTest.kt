package com.wutsi.tracking.manager.service.aggregator.from

import com.wutsi.tracking.manager.Fixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MonthlyFromMapperTest {
    private val mapper = MonthlyFromMapper()

    private val read = Fixtures.createFromEntity(
        productId = "1",
        from = "read-also",
        totalReads = 1000,
    )

    @Test
    fun map() {
        val result = mapper.map(read)
        assertEquals(read.productId, result.key.productId)
        assertEquals(read.from, result.key.from)
        assertEquals(read.totalReads, result.value)
    }
}
