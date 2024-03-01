package com.wutsi.blog.ads.service.filter

import com.wutsi.blog.ads.domain.AdsEntity
import com.wutsi.blog.ads.dto.AdsImpressionContext
import com.wutsi.blog.ads.dto.SearchAdsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdsExcludeOwnerFilterTest {
    private val filter = AdsExcludeOwnerFilter()

    private val request = SearchAdsRequest(
        impressionContext = AdsImpressionContext(
            blogId = 100
        )
    )

    private val ads = listOf(
        AdsEntity(id = "10", userId = 100),
        AdsEntity(id = "11", userId = 100),
        AdsEntity(id = "20", userId = 200),
        AdsEntity(id = "30", userId = 300),
        AdsEntity(id = "40", userId = 400),
    )

    @Test
    fun filter() {
        val result = filter.filter(request, ads)
        assertEquals(3, result.size)
        assertEquals("20", result[0].id)
        assertEquals("30", result[1].id)
        assertEquals("40", result[2].id)
    }
}